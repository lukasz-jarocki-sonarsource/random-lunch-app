import { ButtonHTMLAttributes } from "react";
import "./Button.css";

export default function Button(props: ButtonHTMLAttributes<HTMLButtonElement>) {
  const { children, ...buttonProps } = props;

  return <button {...buttonProps}>{children}</button>;
}
