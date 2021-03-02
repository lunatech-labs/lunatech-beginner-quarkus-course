import {useHistory} from 'react-router-dom';
import {fade, makeStyles} from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import InputBase from '@material-ui/core/InputBase';
import SearchIcon from '@material-ui/icons/Search';
import {Link} from "@material-ui/core";
import {AvatarMenu} from "./AvatarMenu";

import logo from '../images/logo.png'

const useStyles = makeStyles((theme) => ({
    logo: {
        scale: true,
        width: "4rem",
        height: "4rem",
        maxWidth: 160,
    },
    title: {
        paddingLeft: '1rem',
        flexGrow: 1,
        display: 'none',
        [theme.breakpoints.up('xs')]: {
            display: 'block',
        },
        fontFamily: 'Roboto',
        fontWeight: 'bold',
        fontStyle: 'italic',
        fontSize: '900'
    },
    search: {
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: fade(theme.palette.common.white, 0.15),
        '&:hover': {
            backgroundColor: fade(theme.palette.common.white, 0.25),
        },
        marginLeft: 0,
        width: '100%',
        [theme.breakpoints.up('sm')]: {
            marginLeft: theme.spacing(1),
            width: 'auto',
        },
    },
    searchIcon: {
        padding: theme.spacing(0, 2),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    },
    inputRoot: {
        color: 'inherit',
    },
    inputInput: {
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('sm')]: {
            width: '12ch',
            '&:focus': {
                width: '20ch',
            },
        },
    },
    sectionDesktop: {
        display: 'none',
        [theme.breakpoints.up('md')]: {
            display: 'flex',
        },
    },
    tabContainer: {
        marginRight: '2rem',
        marginLeft: '2rem',
    },
    link: {
        fontWeight: 'bold',
        margin: theme.spacing(3),
    }
}));

function Navbar( props ) {

    const classes = useStyles()
    const history = useHistory()


    const doSearch = (e) => {
        const value = e.target.value
        history.push('/search/' + value)
    }

    return (
        <AppBar position="static">
            <Toolbar>
                <img src={logo} alt="logo" className={classes.logo} />
                <Typography variant="h3" className={classes.title}>
                    Hiquéa
                </Typography>

                <div className={classes.tabContainer}>
                    <Link className={classes.link}  style={{textDecoration: 'none'}} color="inherit" href="/">
                        Home
                    </Link>
                </div>
                {props.featureFlags.productSearch ?
                    <form className={classes.search}>
                        <div className={classes.searchIcon}>
                            <SearchIcon />
                        </div>
                        <InputBase
                            id={'search'}
                            placeholder="Search…"
                            classes={{
                                root: classes.inputRoot,
                                input: classes.inputInput,
                            }}
                            inputProps={{ 'aria-label': 'search' }}
                            onChange={doSearch}
                        />
                    </form>
                : '' }

                <AvatarMenu/>
            </Toolbar>
        </AppBar>
    )
}

export default Navbar;
