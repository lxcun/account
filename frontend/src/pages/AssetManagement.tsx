import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Select, InputNumber, Space, message, Popconfirm, Card, Statistic } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { assetApi } from '../api'
import type { Asset } from '../types'
import { auth } from '../utils/auth'

const assetTypeMap = {
  CASH: '现金',
  BANK: '银行卡',
  ALIPAY: '支付宝',
  WECHAT: '微信',
  STOCK: '股票',
  FUND: '基金',
  OTHER: '其他'
}

export default function AssetManagement() {
  const [assets, setAssets] = useState<Asset[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingAsset, setEditingAsset] = useState<Asset | null>(null)
  const [totalAssets, setTotalAssets] = useState(0)
  const [form] = Form.useForm()
  const isLeader = auth.isLeader()

  const loadAssets = async () => {
    setLoading(true)
    try {
      const res = await assetApi.getAll()
      setAssets(res.data)
      const summary = await assetApi.getSummary()
      setTotalAssets(summary.data.totalAssets)
    } catch (error) {
      console.error('加载资产失败', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAssets()
  }, [])

  const handleAdd = () => {
    setEditingAsset(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: Asset) => {
    setEditingAsset(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await assetApi.delete(id)
      message.success('删除成功')
      loadAssets()
    } catch (error) {
      console.error('删除失败', error)
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingAsset) {
        await assetApi.update(editingAsset.id!, values)
        message.success('更新成功')
      } else {
        await assetApi.create({ ...values, balance: values.balance || 0 })
        message.success('创建成功')
      }
      setModalVisible(false)
      loadAssets()
    } catch (error) {
      console.error('提交失败', error)
    }
  }

  const columns = [
    {
      title: '资产名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '资产类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: keyof typeof assetTypeMap) => assetTypeMap[type]
    },
    {
      title: '余额',
      dataIndex: 'balance',
      key: 'balance',
      render: (balance: number) => `¥${balance.toFixed(2)}`,
      sorter: (a: Asset, b: Asset) => a.balance - b.balance,
    },
    {
      title: '备注',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (time: string) => time ? new Date(time).toLocaleString() : '-'
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: Asset) => (
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
              title="确定删除这个资产吗？"
              onConfirm={() => handleDelete(record.id!)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" danger icon={<DeleteOutlined />}>
                删除
              </Button>
            </Popconfirm>
          </Space>
        ) : null
      ),
    },
  ]

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Statistic 
          title="资产总额" 
          value={totalAssets} 
          precision={2}
          prefix="¥"
          valueStyle={{ color: '#3f8600' }}
        />
      </Card>

      <Card>
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
          <h2 style={{ margin: 0 }}>资产管理</h2>
          {isLeader && (
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增资产
            </Button>
          )}
        </div>

        <Table
          columns={columns}
          dataSource={assets}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingAsset ? '编辑资产' : '新增资产'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="资产名称"
            rules={[{ required: true, message: '请输入资产名称' }]}
          >
            <Input placeholder="例如：工商银行储蓄卡" />
          </Form.Item>

          <Form.Item
            name="type"
            label="资产类型"
            rules={[{ required: true, message: '请选择资产类型' }]}
          >
            <Select placeholder="请选择">
              {Object.entries(assetTypeMap).map(([key, value]) => (
                <Select.Option key={key} value={key}>{value}</Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="balance"
            label="初始余额"
            rules={[{ required: true, message: '请输入初始余额' }]}
          >
            <InputNumber
              style={{ width: '100%' }}
              min={0}
              precision={2}
              placeholder="0.00"
            />
          </Form.Item>

          <Form.Item name="description" label="备注">
            <Input.TextArea rows={3} placeholder="可选填写备注信息" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
