const fs = require('fs')
const path = require('path')
const chalk = require('chalk')

const root = path.resolve('./')
const calendar = path.resolve(root, 'packages', 'lightwindow_calendar')
const main = path.resolve(root, 'packages', 'lightwindow_main')

const calendarBuild = path.resolve(calendar, 'dist', 'build', 'h5')
const mainBuild = path.resolve(main, 'dist')
const outDir = path.resolve(root, 'dist')

if (!fs.existsSync(calendarBuild) || fs.readdirSync(calendarBuild).length === 0) throw Error('calendar dist/build/h5目录不存在或为空，请先打包')
if (!fs.existsSync(mainBuild) || fs.readdirSync(mainBuild).length === 0) throw Error('main dist目录不存在或为空，请先打包')
if (fs.existsSync(outDir)) fs.rmSync(outDir, { recursive: true })

fs.cpSync(mainBuild, outDir, { recursive: true })
fs.cpSync(calendarBuild, path.resolve(outDir, 'calendar'), { recursive: true })
console.log(chalk.green('output compeletely!'))
