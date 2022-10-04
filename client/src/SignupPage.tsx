import { FormEvent, useCallback, useState } from "react";
import { Restaurant, signup } from "./api";
import Button from "./Button";
import Checkbox from "./Checkbox";
import Error from "./Error";
import Field from "./Field";
import Loading from "./Loading";
import { Page } from "./page";
import "./SignupPage.css";

interface Props {
  nav: (page: Page) => void;
}

export default function SignupPage({ nav }: Props) {
  const [name, setName] = useState(localStorage.getItem("name") ?? "");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);

  const [restaurants, setRestaurants] = useState<{ [key: string]: boolean }>({
    [Restaurant.blando]: false,
    [Restaurant.cafe]: false,
    [Restaurant.mamasan]: false,
    [Restaurant.vesuvio]: false,
  });

  const toggleRestaurant = useCallback((restaurant: string, checked: boolean) => {
    restaurants[restaurant] = checked;
    setRestaurants({ ...restaurants });
  }, []);

  const validateForm = useCallback(() => {
    return name.length > 0;
  }, [name]);

  const doSignup = useCallback(
    async (e: FormEvent) => {
      e.preventDefault();
      setLoading(true);
      setError(false);
      const success = await signup({
        name,
        restaurants: Object.keys(restaurants).filter((k) => restaurants[k]),
      });

      setLoading(false);

      if (success) {
        nav(Page.wait);
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

        <div>
          <h3>Restaurants you like</h3>
          {[Restaurant.blando, Restaurant.cafe, Restaurant.mamasan, Restaurant.vesuvio].map(
            (resto) => (
              <p key={resto}>
                <Checkbox
                  label={resto}
                  value={resto}
                  checked={restaurants[resto]}
                  onChange={toggleRestaurant}
                />
              </p>
            )
          )}
        </div>

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
