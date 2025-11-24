import LoyaltyCard from "../LoyaltyCard";

export default function LoyaltyCardExample() {
  return (
    <div className="p-4 max-w-md">
      <LoyaltyCard current={7} target={10} />
    </div>
  );
}
