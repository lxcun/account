import { useState, useEffect } from 'react'
import { Card, Table, Button, Space, Modal, Form, Input, Select, DatePicker, InputNumber, Tag, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { transactionApi, assetApi, liabilityApi } from '../api'
import type { Transaction, Asset, Liability } from '../types'
import type { ColumnsType } from 'antd/es/table'
import { auth } from '../utils/auth'

const { Option } = Select

const INCOME_CATEGORIES = ['工资', '奖金', '投资收益', '兼职', '其他收入']
const EXPENSE_CATEGORIES = ['餐饮', '购物', '交通', '住房', '娱乐', '医疗', '教育', '其他支出']

export default function RecordList() {
  const [data, setData] = useState<Transaction[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Transaction | null>(null)
  const [assets, setAssets] = useState<Asset[]>([])
  const [liabilities, setLiabilities] = useState<Liability[]>([])
  const [form] = Form.useForm()
  const isLeader = auth.isLeader()

  const loadData = async () => {
    setLoading(true)
    try {
      const response = await transactionApi.getAll()
      setData(response.data)
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  const loadAssetsAndLiabilities = async () => {
    try {
      const [assetRes, liabilityRes] = await Promise.all([
        assetApi.getAll(),
        liabilityApi.getAll()
      ])
      setAssets(assetRes.data)
      setLiabilities(liabilityRes.data)
    } catch (error) {
      console.error('加载资产负债失败', error)
    }
  }

  useEffect(() => {
    loadData()
    loadAssetsAndLiabilities()
  }, [])

  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    form.setFieldsValue({
      type: 'EXPENSE',
      transactionDate: dayjs(),
      paymentMethod: 'ASSET',
      accountChangeType: 'ASSET_DECREASE'
    })
    setModalVisible(true)
  }

  const handleEdit = (record: Transaction) => {
    setEditingRecord(record)
    form.setFieldsValue({
      ...record,
      transactionDate: dayjs(record.transactionDate)
    })
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await transactionApi.delete(id)
      message.success('删除成功')
      loadData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      
      // 确保必要字段存在
      if (!values.accountChangeType) {
        message.error('账户变动类型未设置，请重新选择类型')
        return
      }
      
      const data = {
        ...values,
        transactionDate: values.transactionDate.format('YYYY-MM-DDTHH:mm:ss')
      }
      
      console.log('提交数据:', data) // 调试日志

      if (editingRecord?.id) {
        await transactionApi.update(editingRecord.id, data)
        message.success('更新成功')
      } else {
        await transactionApi.create(data)
        message.success('添加成功')
      }

      setModalVisible(false)
      loadData()
      loadAssetsAndLiabilities() // 刷新资产负债
    } catch (error) {
      console.error('提交错误:', error)
    }
  }

  const columns: ColumnsType<Transaction> = [
    {
      title: '日期',
      dataIndex: 'transactionDate',
      key: 'transactionDate',
      width: 180,
      render: (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: string) => (
        <Tag color={type === 'INCOME' ? 'green' : 'red'}>
          {type === 'INCOME' ? '收入' : '支出'}
        </Tag>
      )
    },
    {
      title: '分类',
      dataIndex: 'category',
      key: 'category',
      width: 120
    },
    {
      title: '金额',
      dataIndex: 'amount',
      key: 'amount',
      width: 120,
      render: (amount: number, record: Transaction) => (
        <span style={{ 
          color: record.type === 'INCOME' ? '#52c41a' : '#ff4d4f',
          fontWeight: 'bold'
        }}>
          {record.type === 'INCOME' ? '+' : '-'}¥{amount.toFixed(2)}
        </span>
      )
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record: Transaction) => (
        isLeader ? (
          <Space>
            <Button
              type="link"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
            <Popconfirm
              title="确认删除?"
              onConfirm={() => handleDelete(record.id!)}
              okText="确认"
              cancelText="取消"
            >
              <Button
                type="link"
                danger
                icon={<DeleteOutlined />}
              >
                删除
              </Button>
            </Popconfirm>
          </Space>
        ) : null
      )
    }
  ]

  const categoryOptions = form.getFieldValue('type') === 'INCOME' 
    ? INCOME_CATEGORIES 
    : EXPENSE_CATEGORIES

  const transactionType = Form.useWatch('type', form)
  const paymentMethod = Form.useWatch('paymentMethod', form)

  return (
    <div>
      <Card
        title="记账记录"
        extra={
          isLeader && (
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              添加记录
            </Button>
          )
        }
      >
        <Table
          columns={columns}
          dataSource={data}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingRecord ? '编辑记录' : '添加记录'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        okText="确认"
        cancelText="取消"
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          style={{ marginTop: 20 }}
        >
          <Form.Item
            name="type"
            label="类型"
            rules={[{ required: true, message: '请选择类型' }]}
          >
            <Select 
              placeholder="请选择类型"
              onChange={(value) => {
                form.resetFields(['category', 'paymentMethod', 'assetId', 'liabilityId', 'accountChangeType', 'isRepayment'])
                if (value === 'INCOME') {
                  form.setFieldsValue({ 
                    paymentMethod: 'ASSET',
                    accountChangeType: 'ASSET_INCREASE'
                  })
                } else {
                  form.setFieldsValue({ 
                    paymentMethod: 'ASSET',
                    accountChangeType: 'ASSET_DECREASE'
                  })
                }
              }}
            >
              <Option value="INCOME">收入</Option>
              <Option value="EXPENSE">支出</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="category"
            label="分类"
            rules={[{ required: true, message: '请选择分类' }]}
          >
            <Select placeholder="请选择分类">
              {categoryOptions.map(cat => (
                <Option key={cat} value={cat}>{cat}</Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="amount"
            label="金额"
            rules={[
              { required: true, message: '请输入金额' },
              { type: 'number', min: 0.01, message: '金额必须大于0' }
            ]}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder="请输入金额"
              prefix="¥"
              precision={2}
              min={0.01}
            />
          </Form.Item>

          {/* 收入或支出时显示支付方式 */}
          {transactionType === 'INCOME' && (
            <>
              <Form.Item
                name="paymentMethod"
                label="收款方式"
                rules={[{ required: true, message: '请选择收款方式' }]}
              >
                <Select 
                  placeholder="请选择"
                  onChange={(value) => {
                    form.resetFields(['assetId', 'liabilityId'])
                    form.setFieldsValue({ 
                      accountChangeType: 'ASSET_INCREASE'
                    })
                  }}
                >
                  <Option value="ASSET">资产账户（现金/银行卡等）</Option>
                </Select>
              </Form.Item>
              <Form.Item
                name="assetId"
                label="收款账户"
                rules={[{ required: true, message: '请选择收款账户' }]}
              >
                <Select placeholder="请选择资产账户">
                  {assets.map(asset => (
                    <Option key={asset.id} value={asset.id}>
                      {asset.name} (余额: ¥{asset.balance.toFixed(2)})
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </>
          )}

          {transactionType === 'EXPENSE' && (
            <>
              <Form.Item
                name="paymentMethod"
                label="支付方式"
                rules={[{ required: true, message: '请选择支付方式' }]}
              >
                <Select 
                  placeholder="请选择"
                  onChange={(value) => {
                    form.resetFields(['assetId', 'liabilityId'])
                    if (value === 'ASSET') {
                      form.setFieldsValue({ accountChangeType: 'ASSET_DECREASE' })
                    } else if (value === 'CREDIT_CARD') {
                      form.setFieldsValue({ accountChangeType: 'LIABILITY_ONLY_INCREASE' })
                    } else if (value === 'BORROW') {
                      form.setFieldsValue({ accountChangeType: 'LIABILITY_INCREASE' })
                    }
                  }}
                >
                  <Option value="ASSET">资产账户（现金/银行卡等）</Option>
                  <Option value="CREDIT_CARD">信用卡消费（负债增加）</Option>
                  <Option value="BORROW">借款消费（负债+资产同时增加）</Option>
                </Select>
              </Form.Item>

              {paymentMethod === 'ASSET' && (
                <Form.Item
                  name="assetId"
                  label="扣款账户"
                  rules={[{ required: true, message: '请选择扣款账户' }]}
                >
                  <Select placeholder="请选择资产账户">
                    {assets.map(asset => (
                      <Option key={asset.id} value={asset.id}>
                        {asset.name} (余额: ¥{asset.balance.toFixed(2)})
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              )}

              {paymentMethod === 'CREDIT_CARD' && (
                <Form.Item
                  name="liabilityId"
                  label="信用卡账户"
                  rules={[{ required: true, message: '请选择信用卡账户' }]}
                >
                  <Select placeholder="请选择信用卡账户">
                    {liabilities.filter(l => l.type === 'CREDIT_CARD').map(liability => (
                      <Option key={liability.id} value={liability.id}>
                        {liability.name} (欠款: ¥{liability.balance.toFixed(2)})
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              )}

              {paymentMethod === 'BORROW' && (
                <>
                  <Form.Item
                    name="liabilityId"
                    label="借款账户"
                    rules={[{ required: true, message: '请选择借款账户' }]}
                  >
                    <Select placeholder="请选择负债账户">
                      {liabilities.map(liability => (
                        <Option key={liability.id} value={liability.id}>
                          {liability.name} (欠款: ¥{liability.balance.toFixed(2)})
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                  <Form.Item
                    name="assetId"
                    label="资金到账账户"
                    rules={[{ required: true, message: '请选择到账账户' }]}
                  >
                    <Select placeholder="借款资金到账的资产账户">
                      {assets.map(asset => (
                        <Option key={asset.id} value={asset.id}>
                          {asset.name} (余额: ¥{asset.balance.toFixed(2)})
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </>
              )}
            </>
          )}

          {/* 还款功能 - 作为独立选项 */}
          <Form.Item
            name="isRepayment"
            label="是否还款操作"
            valuePropName="checked"
          >
            <Select
              placeholder="请选择"
              onChange={(value) => {
                if (value === 'true') {
                  form.setFieldsValue({ 
                    type: 'EXPENSE',
                    accountChangeType: 'LIABILITY_DECREASE',
                    paymentMethod: 'REPAYMENT'
                  })
                  form.resetFields(['assetId', 'liabilityId'])
                }
              }}
            >
              <Option value="false">否</Option>
              <Option value="true">是（信用卡/贷款还款）</Option>
            </Select>
          </Form.Item>

          {form.getFieldValue('isRepayment') === 'true' && (
            <>
              <Form.Item
                name="liabilityId"
                label="还款账户"
                rules={[{ required: true, message: '请选择还款账户' }]}
              >
                <Select placeholder="请选择负债账户">
                  {liabilities.map(liability => (
                    <Option key={liability.id} value={liability.id}>
                      {liability.name} (欠款: ¥{liability.balance.toFixed(2)})
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item
                name="assetId"
                label="扣款账户"
                rules={[{ required: true, message: '请选择扣款账户' }]}
              >
                <Select placeholder="从哪个资产账户扣款">
                  {assets.map(asset => (
                    <Option key={asset.id} value={asset.id}>
                      {asset.name} (余额: ¥{asset.balance.toFixed(2)})
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </>
          )}

          {/* 隐藏字段：自动设置accountChangeType */}
          <Form.Item name="accountChangeType" hidden>
            <Input />
          </Form.Item>

          <Form.Item
            name="transactionDate"
            label="交易日期"
            rules={[{ required: true, message: '请选择日期' }]}
          >
            <DatePicker
              showTime
              style={{ width: '100%' }}
              format="YYYY-MM-DD HH:mm"
              placeholder="请选择日期时间"
            />
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea
              rows={3}
              placeholder="请输入描述（可选）"
              maxLength={500}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
