// 模拟资源树数据
export const resourceTreeData = [
  {
    fid: '1',
    fName: '学习笔记',
    isDir: true,
    URL: null,
    children: [
      {
        fid: '11',
        fName: 'Vue学习.md',
        isDir: false,
        URL: '/mock/files/vue-notes.md',
        children: null
      },
      {
        fid: '12',
        fName: 'React基础.md',
        isDir: false,
        URL: '/mock/files/react-basics.md',
        children: null
      },
      {
        fid: '13',
        fName: 'JavaScript进阶',
        isDir: true,
        URL: null,
        children: [
          {
            fid: '131',
            fName: '异步编程.md',
            isDir: false,
            URL: '/mock/files/async-js.md',
            children: null
          },
          {
            fid: '132',
            fName: '函数式编程.md',
            isDir: false,
            URL: '/mock/files/functional-js.md',
            children: null
          }
        ]
      }
    ]
  },
  {
    fid: '2',
    fName: '项目资料',
    isDir: true,
    URL: null,
    children: [
      {
        fid: '21',
        fName: '项目规划.md',
        isDir: false,
        URL: '/mock/files/project-planning.md',
        children: null
      },
      {
        fid: '22',
        fName: '接口文档.md',
        isDir: false,
        URL: '/mock/files/api-docs.md',
        children: null
      }
    ]
  },
  {
    fid: '3',
    fName: 'README.md',
    isDir: false,
    URL: '/mock/files/readme.md',
    children: null
  }
];
