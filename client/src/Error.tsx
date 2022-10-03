import { ReactNode } from "react";
import "./Error.css";

interface Props {
  className?: string;
  children: ReactNode;
  error: boolean;
}

export default function Error(props: Props) {
  const { className, children, error } = props;

  return (
    <div aria-hidden={!error} className={`error ${error ? "" : "hidden"} ${className}`}>
      {children}
    </div>
  );
}
