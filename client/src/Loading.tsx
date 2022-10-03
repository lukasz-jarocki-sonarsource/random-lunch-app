import "./Loading.css";

interface Props {
  className?: string;
}

export default function Loading(props: Props) {
  const { className } = props;
  return <div className={`dual-ring-loading ${className}`} />;
}
