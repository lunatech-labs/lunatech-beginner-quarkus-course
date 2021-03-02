const UglifyJsPlugin = require("uglifyjs-webpack-plugin");

module.exports = {
    entry: {
        entry: './src/index.js',
    },
    output: {
        filename: "bundle.min.js",
        path: __dirname + '/build/static/js/'
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"],
            },
            {
                test: /\.m?js$/,
                exclude: /(node_modules)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env'],
                        plugins: ['@babel/plugin-transform-runtime']
                    }
                }
            },
            {
                test: /\.(jpg|png|svg)$/,
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 25000
                    }
                }
            }
        ],
    },
    plugins: [new UglifyJsPlugin()],
}