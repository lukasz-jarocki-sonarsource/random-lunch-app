import { useCallback } from "react";
import "./Checkbox.css";

interface Props {
  label: string;
  checked: boolean;
  value: string;
  onChange: (value: string, checked: boolean) => void;
}

export default function Checkbox(props: Props) {
  const { label, checked, value } = props;

  const onChange = useCallback(() => {
    props.onChange(value, !checked);
  }, [checked]);

  return (
    <label className="checkbox">
      <input onChange={onChange} type="checkbox" checked={checked} value={value} />
      {label}
    </label>
  );
}
