interface SignupData {
  name: string;
  restaurants: string[];
}

export enum Restaurant {
  vesuvio = "Il Vesuvio",
  blando = "Le Blandonnet",
  cafe = "Aviation",
  mamasan = "Mamasan",
}

export async function signup(data: SignupData) {
  const response = await fetch("/api/lunch/signup", {
    method: "POST",
    body: JSON.stringify(data),
    headers: { "content-type": "application/json" },
  });

  return response.status < 300;
}

export async function getStatus(): Promise<{ name?: string; signedUp: boolean }> {
  const response = await fetch("/api/lunch/status", { method: "GET" });

  if (response.status === 200) {
    return response.json().catch(() => ({ signedUp: false }));
  }

  return { signedUp: false };
}

export async function cancel() {
  const response = await fetch("/api/lunch/cancel", { method: "DELETE" });

  return response.status < 300;
}

const RETRIES = 3;

export async function getMatch(): Promise<{ name: string }> {
  let i = 0;

  while (i < RETRIES) {
    console.log("try", i);
    const response = await fetch("/api/lunch/match", { method: "GET" });

    if (response.status === 200) {
      const result = await response.json().catch(() => false);
      console.log(result);
      if (result) {
        return result;
      }
    }

    if (response.status >= 500) {
      return Promise.reject();
    }

    await new Promise<void>((resolve) =>
      setTimeout(() => {
        resolve();
      }, 1000 * 60)
    );

    i += 1;
  }

  return Promise.reject();
}
