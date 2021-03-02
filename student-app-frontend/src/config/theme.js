import {createMuiTheme, rgbToHex} from '@material-ui/core/styles'
const theme = createMuiTheme({
    typography: {
        fontFamily: [
            '-apple-system',
            'BlinkMacSystemFont',
            '"Segoe UI"',
            'Roboto',
            '"Helvetica Neue"',
            'Arial',
            'sans-serif',
            '"Apple Color Emoji"',
            '"Segoe UI Emoji"',
            '"Segoe UI Symbol"',
        ].join(','),
    },
    palette: {
        // In case we want to override default values
        // primary: {main: '#EB2027'},
        // warning: {main :"#EB2027"},
        // black: { dark: '#252525'},
        // white: '#FFFFFF',
        // grey: { main: '#cccccc'}
    },
})
export default theme