import { Layout as AntLayout, Menu, Dropdown, Avatar } from 'antd'
import { HomeOutlined, BarChartOutlined, DollarOutlined, CreditCardOutlined, UserOutlined, LogoutOutlined, FileTextOutlined } from '@ant-design/icons'
import { useNavigate, useLocation } from 'react-router-dom'
import type { ReactNode } from 'react'
import type { MenuProps } from 'antd'
import { auth } from '../utils/auth'

const { Header, Content } = AntLayout

interface Props {
  children: ReactNode
}

export default function Layout({ children }: Props) {
  const navigate = useNavigate()
  const location = useLocation()
  const user = auth.getUser()

  // 支持多种角色值: 'LEADER', '领导', 'admin'等
  const isLeader = user?.role === 'LEADER' ||
                   user?.role === '领导' ||
                   user?.role === 'ADMIN' ||
                   auth.isLeader()

  console.log('Layout - user:', user)
  console.log('Layout - isLeader:', isLeader)

  const menuItems = [
    {
      key: '/records',
      icon: <HomeOutlined />,
      label: '记账记录'
    },
    {
      key: '/assets',
      icon: <DollarOutlined />,
      label: '资产管理'
    },
    {
      key: '/liabilities',
      icon: <CreditCardOutlined />,
      label: '负债管理'
    },
    {
      key: '/statistics',
      icon: <BarChartOutlined />,
      label: '统计汇总'
    },
    ...(isLeader ? [{
      key: '/operation-logs',
      icon: <FileTextOutlined />,
      label: '操作日志'
    }] : [])
  ]

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'role',
      icon: <UserOutlined />,
      label: `角色: ${user?.role}`,
      disabled: true
    },
    {
      type: 'divider'
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: () => {
        auth.logout()
        navigate('/login')
      }
    }
  ]

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', padding: '0 20px' }}>
        <div style={{
          color: 'white',
          fontSize: '20px',
          fontWeight: 'bold',
          marginRight: '40px'
        }}>
          个人记账单
        </div>
        <Menu
          theme="dark"
          mode="horizontal"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
          style={{ flex: 1, minWidth: 0 }}
        />
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          {/* 临时调试信息 - 可以删除 */}
          <div style={{
            color: '#52c41a',
            fontSize: '12px',
            padding: '4px 8px',
            borderRadius: '4px',
            backgroundColor: 'rgba(82, 196, 26, 0.2)'
          }}>
            角色: {user?.role} | 操作日志: {isLeader ? '可见' : '不可见'}
          </div>
          <span style={{ color: 'white', fontSize: '14px' }}>
            {user?.realName}
          </span>
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <Avatar
              icon={<UserOutlined />}
              style={{ backgroundColor: '#1890ff', cursor: 'pointer' }}
            />
          </Dropdown>
        </div>
      </Header>
      <Content style={{ padding: '24px', minHeight: 'calc(100vh - 64px)' }}>
        {children}
      </Content>
    </AntLayout>
  )
}
