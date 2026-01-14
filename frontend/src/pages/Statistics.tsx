import { useState, useEffect } from 'react'
import { Card, DatePicker, Row, Col, Statistic, Table, message, Space, Divider } from 'antd'
import { ArrowUpOutlined, ArrowDownOutlined, DollarOutlined, WalletOutlined, CreditCardOutlined, AccountBookOutlined } from '@ant-design/icons'
import dayjs, { Dayjs } from 'dayjs'
import { transactionApi, assetApi, liabilityApi } from '../api'
import type { Transaction, Summary, Asset, Liability } from '../types'
import type { ColumnsType } from 'antd/es/table'

const { RangePicker } = DatePicker

const assetTypeMap: Record<string, string> = {
  CASH: '现金',
  BANK: '银行卡',
  ALIPAY: '支付宝',
  WECHAT: '微信',
  STOCK: '股票',
  FUND: '基金',
  OTHER: '其他'
}

const liabilityTypeMap: Record<string, string> = {
  CREDIT_CARD: '信用卡',
  MORTGAGE: '房贷',
  CAR_LOAN: '车贷',
  PERSONAL_LOAN: '个人贷款',
  OTHER: '其他'
}

export default function Statistics() {
  const [dateRange, setDateRange] = useState<[Dayjs, Dayjs]>([
    dayjs().startOf('month'),
    dayjs().endOf('month')
  ])
  const [summary, setSummary] = useState<Summary>({
    totalIncome: 0,
    totalExpense: 0,
    balance: 0
  })
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [assets, setAssets] = useState<Asset[]>([])
  const [liabilities, setLiabilities] = useState<Liability[]>([])
  const [totalAssets, setTotalAssets] = useState(0)
  const [totalLiabilities, setTotalLiabilities] = useState(0)
  const [loading, setLoading] = useState(false)

  const loadData = async () => {
    setLoading(true)
    try {
      const [start, end] = dateRange
      const startDate = start.format('YYYY-MM-DDTHH:mm:ss')
      const endDate = end.format('YYYY-MM-DDTHH:mm:ss')

      const [summaryRes, transactionsRes, assetRes, liabilityRes, assetSummaryRes, liabilitySummaryRes] = await Promise.all([
        transactionApi.getSummary(startDate, endDate),
        transactionApi.getByDateRange(startDate, endDate),
        assetApi.getAll(),
        liabilityApi.getAll(),
        assetApi.getSummary(),
        liabilityApi.getSummary()
      ])

      setSummary(summaryRes.data)
      setTransactions(transactionsRes.data)
      setAssets(assetRes.data)
      setLiabilities(liabilityRes.data)
      setTotalAssets(assetSummaryRes.data.totalAssets)
      setTotalLiabilities(liabilitySummaryRes.data.totalLiabilities)
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [dateRange])

  const handleDateChange = (dates: any) => {
    if (dates && dates.length === 2) {
      setDateRange([dates[0], dates[1]])
    }
  }

  const quickDateRanges = {
    '本月': [dayjs().startOf('month'), dayjs().endOf('month')],
    '上月': [dayjs().subtract(1, 'month').startOf('month'), dayjs().subtract(1, 'month').endOf('month')],
    '本年': [dayjs().startOf('year'), dayjs().endOf('year')],
    '最近7天': [dayjs().subtract(6, 'day').startOf('day'), dayjs().endOf('day')],
    '最近30天': [dayjs().subtract(29, 'day').startOf('day'), dayjs().endOf('day')]
  }

  const netWorth = totalAssets - totalLiabilities

  const transactionColumns: ColumnsType<Transaction> = [
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
      render: (type: string) => type === 'INCOME' ? '收入' : '支出'
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
    }
  ]

  const assetColumns: ColumnsType<Asset> = [
    {
      title: '资产名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => assetTypeMap[type] || type
    },
    {
      title: '余额',
      dataIndex: 'balance',
      key: 'balance',
      render: (balance: number) => `¥${balance.toFixed(2)}`,
      sorter: (a: Asset, b: Asset) => a.balance - b.balance,
    }
  ]

  const liabilityColumns: ColumnsType<Liability> = [
    {
      title: '负债名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => liabilityTypeMap[type] || type
    },
    {
      title: '欠款金额',
      dataIndex: 'balance',
      key: 'balance',
      render: (balance: number) => `¥${balance.toFixed(2)}`,
      sorter: (a: Liability, b: Liability) => a.balance - b.balance,
    }
  ]

  return (
    <div>
      {/* 资产负债总览 */}
      <Card title="资产负债总览" style={{ marginBottom: 24 }}>
        <Row gutter={16}>
          <Col span={6}>
            <Card bordered={false} style={{ background: '#f0f9ff' }}>
              <Statistic
                title="总资产"
                value={totalAssets}
                precision={2}
                valueStyle={{ color: '#1890ff' }}
                prefix={<WalletOutlined />}
                suffix="元"
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card bordered={false} style={{ background: '#fff1f0' }}>
              <Statistic
                title="总负债"
                value={totalLiabilities}
                precision={2}
                valueStyle={{ color: '#ff4d4f' }}
                prefix={<CreditCardOutlined />}
                suffix="元"
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card bordered={false} style={{ background: '#f6ffed' }}>
              <Statistic
                title="净资产"
                value={netWorth}
                precision={2}
                valueStyle={{ color: netWorth >= 0 ? '#52c41a' : '#ff4d4f' }}
                prefix={<AccountBookOutlined />}
                suffix="元"
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card bordered={false} style={{ background: '#fff7e6' }}>
              <Statistic
                title="资产负债率"
                value={totalAssets > 0 ? (totalLiabilities / totalAssets * 100) : 0}
                precision={2}
                valueStyle={{ 
                  color: totalAssets > 0 && (totalLiabilities / totalAssets) > 0.7 ? '#ff4d4f' : '#1890ff'
                }}
                suffix="%"
              />
            </Card>
          </Col>
        </Row>

        <Divider />

        <Row gutter={16}>
          <Col span={12}>
            <h3>资产明细</h3>
            <Table
              columns={assetColumns}
              dataSource={assets}
              rowKey="id"
              pagination={false}
              size="small"
              scroll={{ y: 300 }}
            />
          </Col>
          <Col span={12}>
            <h3>负债明细</h3>
            <Table
              columns={liabilityColumns}
              dataSource={liabilities}
              rowKey="id"
              pagination={false}
              size="small"
              scroll={{ y: 300 }}
            />
          </Col>
        </Row>
      </Card>

      {/* 收支统计 */}
      <Card title="收支统计" style={{ marginBottom: 24 }}>
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          <div>
            <span style={{ marginRight: 16, fontWeight: 500 }}>选择时间范围:</span>
            <RangePicker
              value={dateRange}
              onChange={handleDateChange}
              format="YYYY-MM-DD"
              presets={Object.entries(quickDateRanges).map(([label, range]) => ({
                label,
                value: range as [Dayjs, Dayjs]
              }))}
            />
          </div>

          <Row gutter={16}>
            <Col span={8}>
              <Card bordered={false} style={{ background: '#f0f9ff' }}>
                <Statistic
                  title="总收入"
                  value={summary.totalIncome}
                  precision={2}
                  valueStyle={{ color: '#52c41a' }}
                  prefix={<ArrowUpOutlined />}
                  suffix="元"
                />
              </Card>
            </Col>
            <Col span={8}>
              <Card bordered={false} style={{ background: '#fff1f0' }}>
                <Statistic
                  title="总支出"
                  value={summary.totalExpense}
                  precision={2}
                  valueStyle={{ color: '#ff4d4f' }}
                  prefix={<ArrowDownOutlined />}
                  suffix="元"
                />
              </Card>
            </Col>
            <Col span={8}>
              <Card bordered={false} style={{ background: '#fffbe6' }}>
                <Statistic
                  title="收支结余"
                  value={summary.balance}
                  precision={2}
                  valueStyle={{ color: summary.balance >= 0 ? '#1890ff' : '#ff4d4f' }}
                  prefix={<DollarOutlined />}
                  suffix="元"
                />
              </Card>
            </Col>
          </Row>
        </Space>
      </Card>

      {/* 交易明细 */}
      <Card title="交易明细">
        <Table
          columns={transactionColumns}
          dataSource={transactions}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </div>
  )
}
