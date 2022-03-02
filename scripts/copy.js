const fs = require('fs')

fs.rmSync('app/app/src/main/assets', { recursive: true })
fs.cpSync('web/dist', 'app/app/src/main/assets', { recursive: true })
fs.rmSync('app/app/src/main/assets/version.json')
