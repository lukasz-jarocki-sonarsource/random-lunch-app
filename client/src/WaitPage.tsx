import { differenceInMilliseconds, formatDistanceStrict, parse } from "date-fns";
import { useCallback, useEffect, useState } from "react";
import { cancel } from "./api";
import Button from "./Button";
import Error from "./Error";
import Loading from "./Loading";
import { Page } from "./page";
import "./WaitPage.css";

const TIME = "11:30:00";

interface Props {
  nav: (page: Page) => void;
}

export default function WaitPage({ nav }: Props) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const [timer, setTimer] = useState("");

  const doCancel = useCallback(async () => {
    setLoading(true);

    const success = await cancel();

    setLoading(false);

    if (success) {
      nav(Page.signup);
    } else {
      setError(true);
    }
  }, []);

  useEffect(() => {
    setInterval(() => {
      const now = new Date();
      setTimer(formatDistanceStrict(now, parse(TIME, "HH:mm:ss", now)));
    }, 1000);
  }, []);

  useEffect(() => {
    const now = new Date();
    const waitTime = differenceInMilliseconds(parse(TIME, "HH:mm:ss", now), now);

    setTimeout(() => {
      nav(Page.match);
    }, Math.max(0, waitTime));
  }, []);

  return (
    <div id="wait-page" className="card">
      <h2>You've signed up to today's lunch!</h2>

      <p className="margin-l-top">
        We'll find you a good match a few minutes before lunchtime and let you know at that moment!
      </p>

      <p className="margin-xl-top">
        Be patient, you'll be notified in <em>{timer}</em>.
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
