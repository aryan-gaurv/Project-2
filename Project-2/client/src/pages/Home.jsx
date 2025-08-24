import Header from "../componnents/Header";
import Menubar from "../componnents/Menubar";

const Home=()=>{

    return <>
    <div className="flex flex-column items-center justify-content-center min-vh-100">
        <Menubar/>
        <Header/>
    </div>
    </>
}


export default Home;