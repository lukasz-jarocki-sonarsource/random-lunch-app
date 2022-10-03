import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function Redirects() {
  const nav = useNavigate();

  useEffect(() => {
    nav("/signup");
  });

  return null;
}
