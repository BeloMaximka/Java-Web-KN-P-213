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
        case 'authenticate' :
            window.localStorage.setItem("auth-user", JSON.stringify(action.payload));
        // }
        case 'navigate':
            window.location.hash = action.payload;
            return { ...state,
                page: action.payload,
            };
        case 'logout' :
            window.localStorage.removeItem( "auth-user" );
            return { ...state,
                authUser: null,
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
        let authUser = window.localStorage.getItem( "auth-user" );
        if( authUser ) {
            authUser = JSON.parse( authUser );
            let token = authUser.token;
            if( token ) {
                let exp = new Date(token.exp);
                if( exp < new Date() ) {
                    dispatch({type: 'logout'});
                }
                else {
                    dispatch({type: 'authenticate', payload: authUser});
                }
            }
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

                        {!state.authUser && <div>
                            <button type="button" className="btn btn-outline-secondary"
                                    data-bs-toggle="modal" data-bs-target="#authModal">
                                <i className="bi bi-box-arrow-in-right"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary"
                                    onClick={() => dispatch({type: 'navigate', payload: 'signup'})}>
                                <i className="bi bi-person-add"></i>
                            </button>
                        </div>}
                        {state.authUser && <div>
                            <b>{state.authUser.userName}</b>
                            <button type="button" className="btn btn-outline-warning"
                                    onClick={() => dispatch({type: 'logout'})}>
                                <i className="bi bi-box-arrow-right"></i>
                            </button>
                        </div>}

                        <AuthModal></AuthModal>

                    </div>
                </div>
            </nav>
        </header>
        <main className="container">
            {state.page === 'home' && <Home/>}
            {state.page === 'cart' && <Cart/>}
            {state.page === 'signup' && <Signup/>}
            {state.page === 'authtest' && <AuthTest/>}
        </main>
        <div className="spacer"></div>
        <footer className="bg-body-tertiary px-3 py-2">
            &copy; 2024, ITSTEP KN-P-213
        </footer>

        <b onClick={() => dispatch({type: "navigate", payload: "home"})}>Домашня</b>
        <b onClick={() => dispatch({type: "navigate", payload: "cart"})}>Кошик</b>
        <b onClick={() => dispatch({type: "navigate", payload: "authtest"})}>AuthTest</b>
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

function AuthTest() {
    const {contextPath} = React.useContext(AppContext);
    const [results, setResults] = React.useState([]);

    const sendRequest = async (config, expectedStatusCode, expectedStatus) => {
        try {
            const response = await fetch(config.url, {
                method: config.method,
                headers: config.headers,
            });

            const data = await response.json();
            const { code } = data.status;
            const isCorrect = code === expectedStatusCode

            setResults(prevResults => [
                ...prevResults,
                {
                    request: config,
                    response: data,
                    isCorrect,
                }
            ]);
        } catch (error) {
            const response = { code: 500, status: 'error', data: 'Internal Server Error' };
            const isCorrect = false;

            setResults(prevResults => [
                ...prevResults,
                {
                    request: config,
                    response,
                    isCorrect,
                }
            ]);
        }
    };

    const runTests = () => {
        setResults([]);

        sendRequest(
            {
                method: 'GET',
                url: `${contextPath}/auth`,
            },
            401,
            'error'
        );

        sendRequest(
            {
                method: 'GET',
                url: `${contextPath}/auth`,
                headers: {
                    Authorization: 'Bearer sometoken',
                },
            },
            400,
            'error'
        );

        sendRequest(
            {
                method: 'GET',
                url: `${contextPath}/auth`,
                headers: {
                    Authorization: 'Basic ' + btoa('234:123'),
                },
            },
            401,
            'success'
        );

        sendRequest(
            {
                method: 'GET',
                url: `${contextPath}/auth`,
                headers: {
                    Authorization: 'Basic ' + btoa('admin:root'),
                },
            },
            200,
            'success'
        );
    };

    return (
        <div>
            <h1>Automated AUTH Testing</h1>
            <button onClick={runTests}>Run Tests</button>
            <div>
                <div className="accordion accordion-flush" id="accordionFlushExample">
                    {results.map((result, index) => (
                        <div key={'test' + index} className="accordion-item" style={{
                            backgroundColor: result.isCorrect ? 'lightgreen' : 'lightcoral',
                            padding: '10px',
                            margin: '10px 0'
                        }}>
                            <h2 className="accordion-header">
                                <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" style={{
                                    backgroundColor: result.isCorrect ? 'lightgreen' : 'lightcoral',
                                    padding: '10px',
                                    margin: '10px 0'
                                }}
                                        data-bs-target={'#test' + index} >
                                    Request {index + 1}: {result.isCorrect ? 'PASS' : 'FAIL'}
                                </button>
                            </h2>
                            <div id={'test' + index} className="accordion-collapse collapse"
                                 data-bs-parent="#accordionFlushExample">
                                <div className="accordion-body">
                                    <div key={index} style={{
                                        backgroundColor: result.isCorrect ? 'lightgreen' : 'lightcoral',
                                        padding: '10px',
                                        margin: '10px 0'
                                    }}>
                                        <h3>Request {index + 1}</h3>
                                        <pre>{JSON.stringify(result.request, null, 2)}</pre>
                                        <h3>Response:</h3>
                                        <pre>{JSON.stringify(result.response, null, 2)}</pre>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

function Signup() {
    const {contextPath} = React.useContext(AppContext);
    const onFormSubmit = React.useCallback(e => {
        e.preventDefault();
        const formData = new FormData(e.target);
        fetch(`${contextPath}/auth`, {
            method: "POST",
            body: formData,
        }).then((r => r.json().then(console.log)));
    });
    return <div>
        <h1>Реєстрація нового користувача</h1>
        <form encType="multipart/form-data" method="POST" onSubmit={onFormSubmit}>
            <div className="row">
                <div className="col col-6">
                    <div className="input-group mb-3">
                        <span className="input-group-text" id="name-addon"><i className="bi bi-person-badge"></i></span>
                        <input type="text" className="form-control"
                               name="signup-name" placeholder="Ім'я"
                               aria-label="Ім'я" aria-describedby="name-addon"/>
                    </div>
                </div>
                <div className="col col-6">
                    <div className="input-group mb-3">
                        <span className="input-group-text" id="birthdate-addon"><i className="bi bi-cake"></i></span>
                        <input type="date" className="form-control"
                               name="signup-birthdate" placeholder="Дата народження"
                               aria-label="Дата народження" aria-describedby="birthdate-addon"/>
                    </div>
                </div>
            </div>
            <div className="row">
                <div className="col col-6">
                    <div className="input-group mb-3">
                        <span className="input-group-text" id="phone-addon"><i className="bi bi-phone"></i></span>
                        <input type="text" className="form-control"
                               name="signup-phone" placeholder="Телефон"
                               aria-label="Телефон" aria-describedby="phone-addon"/>
                    </div>
                </div>
                <div className="col col-6">
                    <div className="input-group mb-3">
                    <span className="input-group-text" id="email-addon"><i className="bi bi-envelope-at"></i></span>
                        <input type="text" className="form-control"
                               name="signup-email" placeholder="Ел. пошта"
                               aria-label="Ел. пошта" aria-describedby="email-addon"/>
                    </div>
                </div>
            </div>
            <div className="row">
                <div className="col col-6">
                    <div className="input-group mb-3">
                    <span className="input-group-text" id="login-addon"><i
                        className="bi bi-box-arrow-in-right"></i></span>
                        <input type="text" className="form-control"
                               name="signup-login" placeholder="Логін"
                               aria-label="Логін" aria-describedby="login-addon"/>
                    </div>
                </div>
                <div className="col col-6">
                    <div className="input-group mb-3">
                        <label className="input-group-text" htmlFor="signup-avatar"><i className="bi bi-person-circle"></i></label>
                        <input type="file" className="form-control" name="signup-avatar" id="signup-avatar"/>
                    </div>
                </div>
            </div>
            <div className="row">
                <div className="col col-6">
                    <div className="input-group mb-3">
                        <span className="input-group-text" id="password-addon"><i className="bi bi-lock"></i></span>
                        <input type="text" className="form-control"
                               name="signup-password" placeholder="Вигадайте пароль"
                               aria-label="Вигадайте пароль" aria-describedby="password-addon"/>
                    </div>
                </div>
                <div className="col col-6">
                    <div className="input-group mb-3">
                        <span className="input-group-text" id="repeat-addon"><i className="bi bi-unlock"></i></span>
                        <input type="text" className="form-control"
                               name="signup-repeat" placeholder="Повторіть пароль"
                               aria-label="Повторіть пароль" aria-describedby="repeat-addon"/>
                    </div>
                </div>
            </div>
            <div className="row">
                <button type="submit" className="btn btn-outline-success">Реєстрація</button>
            </div>
        </form>
    </div>
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
        <b>Administrator</b>
        <button type="button" className="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#exampleModal">
            <i className="bi bi-box-arrow-right"></i>
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
