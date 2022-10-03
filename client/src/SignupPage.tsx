import { FormEvent, useCallback, useState } from "react";
import { useNavigate } from "react-router-dom";
import { signup } from "./api";
import Button from "./Button";
import Error from "./Error";
import Field from "./Field";
import Loading from "./Loading";
import "./SignupPage.css";

export default function SignupPage() {
  const [name, setName] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const nav = useNavigate();

  const validateForm = useCallback(() => {
    return name.length > 0;
  }, [name]);

  const doSignup = useCallback(
    async (e: FormEvent) => {
      e.preventDefault();
      setLoading(true);
      setError(false);
      const success = await signup({ name });

      setLoading(false);

      if (success) {
        nav("/pending");
      } else {
        setError(true);
      }
    },
    [name]
  );

  return (
    <div id="signup-page" className="card">
      <h2>Please sign up to today's lunch</h2>

      <form className="signup-form" onSubmit={doSignup}>
        <Field
          className="margin-l-top margin-l-bottom"
          label="Name"
          required={true}
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <Error className="margin-l-top" error={error}>
          Something happened :/
        </Error>

        <Button className="margin-l-top" disabled={loading || !validateForm()} type="submit">
          {loading && <Loading className="small margin-m-right" />}
          Sign me up!
        </Button>
      </form>
    </div>
  );
}
