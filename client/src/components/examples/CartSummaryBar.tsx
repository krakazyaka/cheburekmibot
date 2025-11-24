import CartSummaryBar from "../CartSummaryBar";

export default function CartSummaryBarExample() {
  return (
    <div className="h-screen">
      <CartSummaryBar
        itemCount={3}
        total={17.97}
        onClick={() => console.log("View cart clicked")}
      />
    </div>
  );
}
