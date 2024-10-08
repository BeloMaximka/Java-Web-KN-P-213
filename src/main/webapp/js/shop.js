const initialState = {
    auth: {
        token: null,
        user: null
    },
    page: 'home',
};

const AppContext = React.createContext(null);

function reducer( state, action ) {
    switch( action.type ) {
        case 'navigate':
            window.location.hash = action.payload;
            return { ...state,
                page: action.payload,
            };
    }
}

function App({contextPath}) {
    const [state, dispatch] = React.useReducer( reducer, initialState );
    React.useEffect( () => {
        let hash = window.location.hash;
        if( hash.length > 1 ) {
            dispatch( { type: "navigate", payload: hash.substring(1) } );
        }
    }, [] );
    return <AppContext.Provider value={{state, dispatch, contextPath}}>
        <header>
            <nav className="navbar navbar-expand-lg bg-body-tertiary">
                <div className="container-fluid">
                    <a className="navbar-brand" href={contextPath + "/"}>Крамниця</a>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                            data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                            aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarSupportedContent">
                        <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                            <li className="nav-item">
                                <a className="nav-link"
                                   href="/">Home</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link "
                                   href="/web-xml">WebXml</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="/shop.jsp">Крамниця</a>
                            </li>

                        </ul>

                        <form className="d-flex" role="search">
                            <input className="form-control me-2" type="search" placeholder="Search"
                                   aria-label="Search"/>
                            <button className="btn btn-outline-success" type="submit">Search</button>
                        </form>

                        <AuthModal></AuthModal>

                    </div>
                </div>
            </nav>
        </header>
        <main className="container">
            {state.page === 'home' && <Home/>}
            {state.page === 'cart' && <Cart/>}
        </main>
        <div className="spacer"></div>
        <footer className="bg-body-tertiary px-3 py-2">
            &copy; 2024, ITSTEP KN-P-213
        </footer>

        <b onClick={() => dispatch({type: "navigate", payload: "home"})}>Домашня</b>
        <b onClick={() => dispatch({type: "navigate", payload: "cart"})}>Кошик</b>
    </AppContext.Provider>;
}

function Cart() {
    const {state, dispatch} = React.useContext(AppContext);
    return <div>
        <h2>Кошик</h2>
        <b onClick={() => dispatch({type: "navigate", payload: "home"})}>На Домашню</b>
    </div>;
}

function Home() {
    const {state, dispatch} = React.useContext(AppContext);
    return <div>
        <h2>Домашня</h2>
        <b onClick={() => dispatch({type: "navigate", payload: "cart"})}>До Кошику</b>
    </div>;
}

function AuthModal() {
    const {contextPath} = React.useContext( AppContext );
    const [login, setLogin] = React.useState("");
    const [password, setPassword] = React.useState("");
    const authClick = React.useCallback( () => {
        console.log(login, password);
        fetch(`${contextPath}/auth`, {
            method: 'GET',
            headers: {
                'Authorization': 'Basic ' + btoa( login + ':' + password)
            }
        }).then(r => r.json()).then(console.log);
    });
    return <div>
        <button type="button" className="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
            <i className="bi bi-box-arrow-in-right"></i>
        </button>

        <div className="modal fade" id="exampleModal" tabIndex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div className="modal-dialog">
                <div className="modal-content">
                    <div className="modal-header">
                        <h1 className="modal-title fs-5" id="exampleModalLabel">Увійти в систему</h1>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div className="modal-body">
                        <div className="input-group mb-3">
                            <span className="input-group-text" id="basic-addon1">
                                <i className="bi bi-person-fill-lock"></i>
                            </span>
                            <input type="text" className="form-control" placeholder="Логін" aria-label="Логін"
                                   onChange={e => setLogin(e.target.value)}
                                   aria-describedby="login-addon"/>
                        </div>
                        <div className="input-group mb-3">
                            <span className="input-group-text" id="basic-addon1">
                               <i className="bi bi-key-fill"></i>
                            </span>
                            <input type="password" className="form-control" placeholder="******" aria-label="Пароль"
                                   onChange={e => setPassword(e.target.value)}
                                   aria-describedby="password-addon"/>
                        </div>

                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Скасувати</button>
                        <button type="button" className="btn btn-primary" onClick={authClick}>Вхід</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
}

const domRoot = document.getElementById("app-container");
const cp = domRoot.getAttribute("data-context-path");
ReactDOM
    .createRoot(domRoot)
    .render(<App contextPath={cp}/>);
