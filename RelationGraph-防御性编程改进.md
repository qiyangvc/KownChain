# RelationGraph 防御性编程改进

## 概述
为 RelationGraph 组件添加了与 WorkbenchView 一致的防御性编程，确保前后端数据结构不匹配时的稳定性。

## 主要改进点

### 1. collectFiles 函数增强
- **现有改进**: 已经有基础的防御性检查
- **新增改进**: 更严格的字段名兼容性处理

### 2. buildGraph 函数完全重构
**原代码问题**:
```javascript
// 没有防御性检查，容易因字段缺失导致异常
nodes.value.push({
  id: item.fid,           // 可能为undefined
  label: item.fName,      // 可能为undefined
  isDir: !!item.isDir,
  level
});
```

**新代码改进**:
```javascript
// 防御性检查：确保item是有效对象
if (!item || typeof item !== 'object') return;

// 统一字段名处理（支持多种可能的字段名）
const fileName = item.fName || item.fname || item.name || 'unnamed';
const fileId = item.fid || item.id || item.URL;
const isDirectory = item.isDir || item.dir || false;
const children = item.children || item.child || [];

// 确保节点有有效的ID
if (!fileId) return;
```

### 3. buildContentLinks 函数增强
**防御性改进**:
- 参数有效性检查：`if (!files || !Array.isArray(files)) return;`
- 文件对象有效性检查：`if (!file || typeof file !== 'object') continue;`
- API响应数据验证：`if (res && res.data && typeof res.data === 'string')`
- 避免自引用：`if (targetFid && targetFid !== fileId)`
- 错误处理：捕获并记录API调用失败

### 4. initGraph 函数增强
**防御性改进**:
- 添加 try-catch 错误处理
- 资源树数据验证：`if (!props.resourceTree || !Array.isArray(props.resourceTree))`
- 确保在 finally 块中设置 loading 状态

### 5. renderGraph 函数增强
**防御性改进**:
- DOM元素存在性检查：`if (!graph.value) return;`
- 节点数据过滤：过滤掉无效的节点数据
- 边数据过滤：过滤掉无效的边数据
- 标签安全转换：`String(n.label || 'unnamed')`
- 尺寸计算保护：`Math.max(12, 30 - (n.level || 0) * 6)`
- 网络实例销毁保护：try-catch 包装销毁操作

### 6. 新增生命周期管理
**新增功能**:
- 组件卸载时的清理函数：`onUnmounted(() => cleanup())`
- 智能资源树变化监听：只在数据真正改变时重新初始化

### 7. 字段名兼容性
支持多种字段名格式，与后端数据结构保持兼容：
- 文件名：`fName` || `fname` || `name` || `'unnamed'`
- 文件ID：`fid` || `id` || `URL`
- 目录标识：`isDir` || `dir` || `false`
- 子节点：`children` || `child` || `[]`
- 文件URL：`URL` || `url`

## 错误处理策略

### 1. 静默处理
对于非关键错误，使用 `console.warn` 记录并继续执行：
- API调用失败
- 网络实例销毁失败
- 图谱渲染非致命错误

### 2. 中断处理
对于关键错误，使用 `console.error` 记录并中断执行：
- 初始化图谱失败
- 渲染图谱失败

### 3. 数据验证
- 在每个关键函数入口进行参数验证
- 过滤无效数据而不是抛出异常
- 提供默认值防止 undefined/null 错误

## 与 WorkbenchView 的一致性

### 字段名处理模式
```javascript
// WorkbenchView 模式
const currentFileName = computed(() => {
  if (!currentFile.value) return '';
  return currentFile.value.fName || currentFile.value.fname || '未知文件';
});

// RelationGraph 采用相同模式
const fileName = item.fName || item.fname || item.name || 'unnamed';
```

### 防御性检查模式
```javascript
// WorkbenchView 模式
if (!selectedNode.value) return false;
return selectedNode.value.isDir || selectedNode.value.dir || false;

// RelationGraph 采用相同模式
if (!item || typeof item !== 'object') return;
const isDirectory = item.isDir || item.dir || false;
```

## 测试建议

1. **空数据测试**: 传入空数组或 null 的 resourceTree
2. **字段缺失测试**: 传入缺少关键字段的节点数据
3. **混合字段名测试**: 同时包含 fName/fname/name 等不同字段名的数据
4. **API失败测试**: 模拟文件内容获取失败的情况
5. **大数据量测试**: 测试大量节点和边的渲染性能

## 兼容性保证

- **向后兼容**: 原有的数据结构仍然完全支持
- **向前兼容**: 支持新的字段名格式
- **错误容忍**: 即使部分数据有问题，也能正常显示其他有效数据
- **渐进增强**: 在数据完整时提供完整功能，在数据不完整时提供基础功能
