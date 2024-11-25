# React + Vite

rm -rf node_modules/
npm install

sudo chmod +x node_modules/.bin/vite

npm cache clear --force
# removing the node_modules & lock file
npm install



# run on server
npm run build
npm install -g serve
serve -s dist -l 3000
