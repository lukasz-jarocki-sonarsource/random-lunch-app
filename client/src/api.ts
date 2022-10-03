interface SignupData {
  name: string;
}

export async function signup(data: SignupData) {
  const response = await fetch("/api/lunch/signup", { method: "POST", body: JSON.stringify(data) });

  return response.status < 300;
}

export async function cancel() {
  const response = await fetch("/api/lunch/cancel", { method: "DELETE" });

  return response.status < 300;
}

const RETRIES = 3;

export async function getMatches() {
  let i = 0;

  while (i < RETRIES) {
    const response = await fetch("/api/lunch/matches", { method: "GET" });

    if (response.status === 200) {
      return response.json();
    }

    if (response.status >= 500) {
      return Promise.reject();
    }

    await new Promise<void>((resolve) =>
      setTimeout(() => {
        resolve();
      }, 1000 * 60)
    );
  }

  return Promise.reject();
}
