const fs = require('fs')
const path = require('path')

const root = path.resolve('./')
const calendar = path.resolve(root, 'packages', 'lightwindow_calendar')
const main = path.resolve(root, 'packages', 'lightwindow_main')

// 删除目录
function deleteDir (p) {
  if (!fs.existsSync(p)) return
  const files = fs.readdirSync(p)
  files.forEach(f => {
    const fp = path.resolve(p, f)
    if (fs.statSync(fp).isDirectory()) {
      deleteDir(fp)
    } else {
      fs.unlinkSync(fp)
    }
  })
  fs.rmdirSync(p)
}

// 创建to目录并复制form内所有内容
function copyDir (from, to) {
  if (fs.existsSync(to)) throw Error('文件已存在：' + to)
  fs.mkdirSync(to)
  const files = fs.readdirSync(from)
  files.forEach(f => {
    const fp = path.resolve(from, f)
    const tp = path.resolve(to, f)
    if (fs.statSync(fp).isDirectory()) {
      copyDir(fp, tp)
    } else {
      fs.copyFileSync(fp, tp)
    }
  })
}

module.exports = {
  deleteDir,
  copyDir,
  root,
  main,
  calendar
}
