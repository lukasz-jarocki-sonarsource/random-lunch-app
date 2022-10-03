import { useEffect, useState } from "react";
import { getMatches as getMatch } from "./api";
import Loading from "./Loading";
import "./WaitPage.css";

export default function MatchPage() {
  const [matchName, setMatchName] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const match = await getMatch();

        setMatchName(match.name);
      } catch (e) {
        console.log(e);
        setError(true);
      }
      setLoading(false);
    })();
  }, []);

  if (loading) {
    return (
      <div id="match-page" className="card">
        <Loading />
      </div>
    );
  }

  return (
    <div id="match-page" className="card">
      {error ? (
        <>
          <h2>Oops, something went wrong</h2>
          <p>Try reloading the page, or contact an admin!</p>
        </>
      ) : (
        <>
          <h2>You've been matched for today's lunch!</h2>

          <p className="margin-l-top">🎉️ We've found you a match 🎉️</p>

          <p>Please meet {matchName} at 12:00 by the reception</p>
        </>
      )}
    </div>
  );
}