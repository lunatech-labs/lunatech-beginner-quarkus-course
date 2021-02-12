import { useHistory } from 'react-router-dom';

function Navbar() {
    const history = useHistory();

    const doSearch = (e) => {
        const query = document.getElementById("search").value;
        history.push('/search/' + query);
    }

    return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <a className="navbar-brand" href="/">Hiquéa</a>

        <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <ul className="navbar-nav mr-auto">
                <li className="nav-item active">
                    <a className="nav-link" href="/">Home</a>
                </li>
            </ul>
            <form className="form-inline my-2 my-lg-0">
                <input id="search" className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" onChange={doSearch} />
            </form>
        </div>
    </nav>)
}

export default Navbar;
