import { useEffect, useState } from "react";
import { getStatus } from "./api";
import "./App.css";
import MatchPage from "./MatchPage";
import { Page } from "./page";
import SignupPage from "./SignupPage";
import WaitPage from "./WaitPage";

function renderPage(state: Page, navigate: (page: Page) => void) {
  switch (state) {
    case Page.match:
      return <MatchPage nav={navigate} />;
    case Page.wait:
      return <WaitPage nav={navigate} />;
    case Page.signup:
    default:
      return <SignupPage nav={navigate} />;
  }
}

function App() {
  const [page, navigate] = useState(Page.signup);

  useEffect(() => {
    (async () => {
      const response = await getStatus();

      if (response.signedUp) {
        if (response.name) {
          localStorage.setItem("name", response.name);
        }
        navigate(Page.wait);
      }
    })();
  }, []);

  return (
    <div className="App">
      <h1>Random Lunch Matchmaker</h1>

      <button onClick={() => navigate(Page.signup)}>signup</button>
      <button onClick={() => navigate(Page.wait)}>wait</button>
      <button onClick={() => navigate(Page.match)}>match</button>

      {renderPage(page, navigate)}
    </div>
  );
}

export default App;
