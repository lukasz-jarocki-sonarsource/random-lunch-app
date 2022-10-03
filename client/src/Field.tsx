import { ChangeEventHandler } from "react";
import "./Field.css";

interface Props {
  className?: string;
  label: string;
  onChange: ChangeEventHandler<HTMLInputElement>;
  required: boolean;
  value: string;
}
export default function Input(props: Props) {
  const { className, label, required, value } = props;

  return (
    <label className={`field ${className}`}>
      <span>{label}</span>
      {required && <span className="required">*</span>}
      <input value={value} onChange={props.onChange} />
    </label>
  );
}
