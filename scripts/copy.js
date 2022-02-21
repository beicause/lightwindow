const fs = require('fs')

fs.cpSync('web/dist', 'app/app/src/main/assets', { recursive: true })