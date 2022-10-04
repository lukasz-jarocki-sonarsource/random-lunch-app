import { useState } from "react";
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

  return (
    <div className="App">
      <h1>Random Lunch Matchmaker</h1>

      {renderPage(page, navigate)}
    </div>
  );
}

export default App;
