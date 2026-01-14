import { useState, useEffect } from 'react'
import { Table, Button, Modal, Form, Input, Select, InputNumber, Space, message, Popconfirm, Card, Statistic } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { liabilityApi } from '../api'
import type { Liability } from '../types'
import { auth } from '../utils/auth'

const liabilityTypeMap = {
  CREDIT_CARD: '信用卡',
  MORTGAGE: '房贷',
  CAR_LOAN: '车贷',
  PERSONAL_LOAN: '个人贷款',
  OTHER: '其他'
}

export default function LiabilityManagement() {
  const [liabilities, setLiabilities] = useState<Liability[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingLiability, setEditingLiability] = useState<Liability | null>(null)
  const [totalLiabilities, setTotalLiabilities] = useState(0)
  const [form] = Form.useForm()
  const isLeader = auth.isLeader()

  const loadLiabilities = async () => {
    setLoading(true)
    try {
      const res = await liabilityApi.getAll()
      setLiabilities(res.data)
      const summary = await liabilityApi.getSummary()
      setTotalLiabilities(summary.data.totalLiabilities)
    } catch (error) {
      console.error('加载负债失败', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadLiabilities()
  }, [])

  const handleAdd = () => {
    setEditingLiability(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: Liability) => {
    setEditingLiability(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await liabilityApi.delete(id)
      message.success('删除成功')
      loadLiabilities()
    } catch (error) {
      console.error('删除失败', error)
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingLiability) {
        await liabilityApi.update(editingLiability.id!, values)
        message.success('更新成功')
      } else {
        await liabilityApi.create({ ...values, balance: values.balance || 0 })
        message.success('创建成功')
      }
      setModalVisible(false)
      loadLiabilities()
    } catch (error) {
      console.error('提交失败', error)
    }
  }

  const columns = [
    {
      title: '负债名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '负债类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: keyof typeof liabilityTypeMap) => liabilityTypeMap[type]
    },
    {
      title: '欠款金额',
      dataIndex: 'balance',
      key: 'balance',
      render: (balance: number) => `¥${balance.toFixed(2)}`,
      sorter: (a: Liability, b: Liability) => a.balance - b.balance,
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
      render: (_: any, record: Liability) => (
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
              title="确定删除这个负债吗？"
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
          title="负债总额" 
          value={totalLiabilities} 
          precision={2}
          prefix="¥"
          valueStyle={{ color: '#cf1322' }}
        />
      </Card>

      <Card>
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
          <h2 style={{ margin: 0 }}>负债管理</h2>
          {isLeader && (
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增负债
            </Button>
          )}
        </div>

        <Table
          columns={columns}
          dataSource={liabilities}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>

      <Modal
        title={editingLiability ? '编辑负债' : '新增负债'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="负债名称"
            rules={[{ required: true, message: '请输入负债名称' }]}
          >
            <Input placeholder="例如：招商银行信用卡" />
          </Form.Item>

          <Form.Item
            name="type"
            label="负债类型"
            rules={[{ required: true, message: '请选择负债类型' }]}
          >
            <Select placeholder="请选择">
              {Object.entries(liabilityTypeMap).map(([key, value]) => (
                <Select.Option key={key} value={key}>{value}</Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="balance"
            label="初始欠款金额"
            rules={[{ required: true, message: '请输入初始欠款金额' }]}
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
