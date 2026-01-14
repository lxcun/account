import { useState, useEffect } from 'react'
import { Table, Card, Space, Tag, Button, DatePicker, Select, Input, Drawer, Descriptions, message, Row, Col, Statistic } from 'antd'
import { SearchOutlined, ReloadOutlined, FilterOutlined, FileTextOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import dayjs from 'dayjs'
import { operationLogApi } from '../api'
import type { OperationLog } from '../types'

const { RangePicker } = DatePicker
const { Option } = Select

export default function OperationLogList() {
  const [logs, setLogs] = useState<OperationLog[]>([])
  const [loading, setLoading] = useState(false)
  const [total, setTotal] = useState(0)
  const [selectedLog, setSelectedLog] = useState<OperationLog | null>(null)
  const [detailVisible, setDetailVisible] = useState(false)

  // 查询条件
  const [filters, setFilters] = useState({
    module: undefined as string | undefined,
    operationType: undefined as string | undefined,
    timeRange: undefined as [dayjs.Dayjs, dayjs.Dayjs] | undefined
  })

  const fetchLogs = async () => {
    setLoading(true)
    try {
      let response
      if (filters.timeRange) {
        const [start, end] = filters.timeRange
        response = await operationLogApi.getByTimeRange(start.toISOString(), end.toISOString())
      } else if (filters.module) {
        response = await operationLogApi.getByModule(filters.module)
      } else if (filters.operationType) {
        response = await operationLogApi.getByOperationType(filters.operationType)
      } else {
        response = await operationLogApi.getRecent(100)
      }

      if (response.data) {
        setLogs(response.data)
        setTotal(response.data.length)
      }
    } catch (error) {
      message.error('加载操作日志失败')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = () => {
    fetchLogs()
  }

  const handleReset = () => {
    setFilters({
      module: undefined,
      operationType: undefined,
      timeRange: undefined
    })
    setTimeout(() => fetchLogs(), 100)
  }

  const showDetail = (record: OperationLog) => {
    setSelectedLog(record)
    setDetailVisible(true)
  }

  const formatJson = (jsonStr?: string) => {
    if (!jsonStr) return '-'
    try {
      const obj = JSON.parse(jsonStr)
      return JSON.stringify(obj, null, 2)
    } catch {
      return jsonStr
    }
  }

  const getModuleTag = (module: string) => {
    const colorMap: Record<string, string> = {
      'ASSET': 'blue',
      'LIABILITY': 'orange',
      'TRANSACTION': 'green',
      'OTHER': 'default'
    }
    const labelMap: Record<string, string> = {
      'ASSET': '资产',
      'LIABILITY': '负债',
      'TRANSACTION': '交易',
      'OTHER': '其他'
    }
    return <Tag color={colorMap[module] || 'default'}>{labelMap[module] || module}</Tag>
  }

  const getOperationTypeTag = (type: string) => {
    const colorMap: Record<string, string> = {
      'CREATE': 'green',
      'UPDATE': 'blue',
      'DELETE': 'red',
      'OTHER': 'default'
    }
    const labelMap: Record<string, string> = {
      'CREATE': '创建',
      'UPDATE': '更新',
      'DELETE': '删除',
      'OTHER': '其他'
    }
    return <Tag color={colorMap[type] || 'default'}>{labelMap[type] || type}</Tag>
  }

  const getStatusTag = (status: string) => {
    const colorMap: Record<string, string> = {
      'SUCCESS': 'success',
      'FAILED': 'error'
    }
    const labelMap: Record<string, string> = {
      'SUCCESS': '成功',
      'FAILED': '失败'
    }
    return <Tag color={colorMap[status] || 'default'}>{labelMap[status] || status}</Tag>
  }

  const columns: ColumnsType<OperationLog> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80
    },
    {
      title: '操作人',
      key: 'user',
      width: 150,
      render: (_, record) => (
        <div>
          <div style={{ fontWeight: 'bold' }}>{record.realName || record.username}</div>
          <div style={{ fontSize: '12px', color: '#999' }}>{record.username}</div>
        </div>
      )
    },
    {
      title: '模块',
      dataIndex: 'module',
      key: 'module',
      width: 100,
      render: (module) => getModuleTag(module)
    },
    {
      title: '操作类型',
      dataIndex: 'operationType',
      key: 'operationType',
      width: 100,
      render: (type) => getOperationTypeTag(type)
    },
    {
      title: '操作名称',
      dataIndex: 'operationName',
      key: 'operationName',
      width: 150
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => getStatusTag(status)
    },
    {
      title: '操作时间',
      dataIndex: 'operationTime',
      key: 'operationTime',
      width: 180,
      render: (time) => dayjs(time).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: 'IP地址',
      dataIndex: 'ip',
      key: 'ip',
      width: 130
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Button type="link" size="small" onClick={() => showDetail(record)}>
          查看详情
        </Button>
      )
    }
  ]

  useEffect(() => {
    fetchLogs()
  }, [])

  return (
    <div style={{ padding: '24px' }}>
      <Card
        title={
          <Space>
            <FileTextOutlined />
            操作日志
          </Space>
        }
        extra={
          <Button icon={<ReloadOutlined />} onClick={fetchLogs} loading={loading}>
            刷新
          </Button>
        }
      >
        <Row gutter={16} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Card size="small">
              <Statistic title="总记录数" value={total} />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="创建操作"
                value={logs.filter(l => l.operationType === 'CREATE').length}
                valueStyle={{ color: '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="更新操作"
                value={logs.filter(l => l.operationType === 'UPDATE').length}
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card size="small">
              <Statistic
                title="删除操作"
                value={logs.filter(l => l.operationType === 'DELETE').length}
                valueStyle={{ color: '#ff4d4f' }}
              />
            </Card>
          </Col>
        </Row>

        <Card size="small" style={{ marginBottom: 16 }}>
          <Space size="middle">
            <FilterOutlined />
            <span>筛选条件:</span>
            <Select
              placeholder="选择模块"
              style={{ width: 120 }}
              allowClear
              value={filters.module}
              onChange={(value) => setFilters({ ...filters, module: value })}
            >
              <Option value="ASSET">资产</Option>
              <Option value="LIABILITY">负债</Option>
              <Option value="TRANSACTION">交易</Option>
            </Select>
            <Select
              placeholder="操作类型"
              style={{ width: 120 }}
              allowClear
              value={filters.operationType}
              onChange={(value) => setFilters({ ...filters, operationType: value })}
            >
              <Option value="CREATE">创建</Option>
              <Option value="UPDATE">更新</Option>
              <Option value="DELETE">删除</Option>
            </Select>
            <RangePicker
              showTime
              value={filters.timeRange}
              onChange={(dates) => setFilters({ ...filters, timeRange: dates || undefined })}
            />
            <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
              查询
            </Button>
            <Button onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Card>

        <Table
          columns={columns}
          dataSource={logs}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1200 }}
          pagination={{
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            defaultPageSize: 20
          }}
        />
      </Card>

      <Drawer
        title="操作日志详情"
        placement="right"
        width={600}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {selectedLog && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="日志ID">{selectedLog.id}</Descriptions.Item>
            <Descriptions.Item label="操作人">
              {selectedLog.realName} ({selectedLog.username})
            </Descriptions.Item>
            <Descriptions.Item label="用户ID">{selectedLog.userId}</Descriptions.Item>
            <Descriptions.Item label="模块">
              {getModuleTag(selectedLog.module)}
            </Descriptions.Item>
            <Descriptions.Item label="操作类型">
              {getOperationTypeTag(selectedLog.operationType)}
            </Descriptions.Item>
            <Descriptions.Item label="操作名称">{selectedLog.operationName}</Descriptions.Item>
            <Descriptions.Item label="状态">
              {getStatusTag(selectedLog.status)}
            </Descriptions.Item>
            <Descriptions.Item label="操作时间">
              {dayjs(selectedLog.operationTime).format('YYYY-MM-DD HH:mm:ss')}
            </Descriptions.Item>
            <Descriptions.Item label="IP地址">{selectedLog.ip || '-'}</Descriptions.Item>
            {selectedLog.targetId && (
              <Descriptions.Item label="目标ID">{selectedLog.targetId}</Descriptions.Item>
            )}
            {selectedLog.targetName && (
              <Descriptions.Item label="目标名称">{selectedLog.targetName}</Descriptions.Item>
            )}
            {selectedLog.description && (
              <Descriptions.Item label="描述">{selectedLog.description}</Descriptions.Item>
            )}
            {selectedLog.errorMsg && (
              <Descriptions.Item label="错误信息">
                <span style={{ color: '#ff4d4f' }}>{selectedLog.errorMsg}</span>
              </Descriptions.Item>
            )}
            {selectedLog.requestData && (
              <Descriptions.Item label="请求数据">
                <pre style={{ maxHeight: '200px', overflow: 'auto', fontSize: '12px' }}>
                  {formatJson(selectedLog.requestData)}
                </pre>
              </Descriptions.Item>
            )}
            {selectedLog.resultData && (
              <Descriptions.Item label="结果数据">
                <pre style={{ maxHeight: '200px', overflow: 'auto', fontSize: '12px' }}>
                  {formatJson(selectedLog.resultData)}
                </pre>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Drawer>
    </div>
  )
}
