import { useCallback, useState } from "react";
import { useNavigate } from "react-router-dom";
import { cancel } from "./api";
import Button from "./Button";
import Error from "./Error";
import Loading from "./Loading";
import "./WaitPage.css";

export default function WaitPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const nav = useNavigate();

  const doCancel = useCallback(async () => {
    setLoading(true);

    const success = await cancel();

    setLoading(false);

    if (success) {
      nav("/signup");
    } else {
      setError(true);
    }
  }, []);

  return (
    <div id="wait-page" className="card">
      <h2>You've signed up to today's lunch!</h2>

      <p className="margin-l-top">
        We'll find you a good match a few minutes before lunchtime and let you know at that moment!
      </p>

      <Error className="margin-l-top" error={error}>
        Something happened :/
      </Error>

      <Button className="margin-l-top" disabled={loading} onClick={doCancel} type="button">
        {loading && <Loading className="small margin-m-right" />}
        Cancel
      </Button>
    </div>
  );
}
