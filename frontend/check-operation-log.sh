#!/bin/bash

echo "==================================="
echo "操作日志功能检查脚本"
echo "==================================="
echo ""

# 检查文件是否存在
echo "1. 检查文件是否存在..."
files=(
  "src/App.tsx"
  "src/components/Layout.tsx"
  "src/pages/OperationLogList.tsx"
  "src/utils/auth.ts"
  "src/api/index.ts"
  "src/types/index.ts"
)

all_exist=true
for file in "${files[@]}"; do
  if [ -f "$file" ]; then
    echo "   ✅ $file"
  else
    echo "   ❌ $file (不存在)"
    all_exist=false
  fi
done
echo ""

# 检查关键代码
echo "2. 检查关键代码..."

if grep -q "OperationLogList" src/App.tsx; then
  echo "   ✅ App.tsx 已导入 OperationLogList"
else
  echo "   ❌ App.tsx 未导入 OperationLogList"
  all_exist=false
fi

if grep -q '/operation-logs' src/App.tsx; then
  echo "   ✅ App.tsx 已添加操作日志路由"
else
  echo "   ❌ App.tsx 未添加操作日志路由"
  all_exist=false
fi

if grep -q "FileTextOutlined" src/components/Layout.tsx; then
  echo "   ✅ Layout.tsx 已导入 FileTextOutlined 图标"
else
  echo "   ❌ Layout.tsx 未导入 FileTextOutlined 图标"
  all_exist=false
fi

if grep -q "isLeader" src/components/Layout.tsx; then
  echo "   ✅ Layout.tsx 已添加 isLeader 判断"
else
  echo "   ❌ Layout.tsx 未添加 isLeader 判断"
  all_exist=false
fi

if grep -q "operation-logs" src/components/Layout.tsx; then
  echo "   ✅ Layout.tsx 已添加操作日志菜单"
else
  echo "   ❌ Layout.tsx 未添加操作日志菜单"
  all_exist=false
fi

if grep -q "operationLogApi" src/api/index.ts; then
  echo "   ✅ api/index.ts 已添加 operationLogApi"
else
  echo "   ❌ api/index.ts 未添加 operationLogApi"
  all_exist=false
fi

if grep -q "interface OperationLog" src/types/index.ts; then
  echo "   ✅ types/index.ts 已添加 OperationLog 接口"
else
  echo "   ❌ types/index.ts 未添加 OperationLog 接口"
  all_exist=false
fi

echo ""

# 总结
if [ "$all_exist" = true ]; then
  echo "✅ 所有检查通过! 操作日志功能已正确集成。"
  echo ""
  echo "下一步操作:"
  echo "1. 确保使用领导角色登录 (leader / leader123)"
  echo "2. 重启前端开发服务器: npm run dev"
  echo "3. 访问 http://localhost:5173"
  echo "4. 点击顶部导航的'操作日志'菜单"
else
  echo "❌ 部分检查未通过,请检查上述缺失的文件或代码。"
fi

echo ""
echo "==================================="
