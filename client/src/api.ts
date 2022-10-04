interface SignupData {
  name: string;
}

export async function signup(data: SignupData) {
  const response = await fetch("/api/lunch/signup", {
    method: "POST",
    body: JSON.stringify(data),
    headers: { "content-type": "application/json" },
  });

  return response.status < 300;
}

export async function cancel() {
  const response = await fetch("/api/lunch/cancel", { method: "DELETE" });

  return response.status < 300;
}

const RETRIES = 3;

export async function getMatches(): Promise<{ name: string }> {
  let i = 0;

  while (i < RETRIES) {
    const response = await fetch("/api/lunch/match", { method: "GET" });

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
