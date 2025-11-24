import { Button } from "@/components/ui/button";
import { ShoppingCart } from "lucide-react";

interface CartSummaryBarProps {
  itemCount: number;
  total: number;
  onClick: () => void;
}

export default function CartSummaryBar({
  itemCount,
  total,
  onClick,
}: CartSummaryBarProps) {
  if (itemCount === 0) return null;

  return (
    <div className="fixed bottom-16 left-0 right-0 z-40 px-4 pb-4">
      <Button
        className="w-full max-w-lg mx-auto flex items-center justify-between h-14 shadow-lg"
        size="lg"
        onClick={onClick}
        data-testid="button-view-cart"
      >
        <div className="flex items-center gap-2">
          <ShoppingCart className="w-5 h-5" />
          <span className="font-medium" data-testid="text-cart-count">
            {itemCount} {itemCount === 1 ? "товар" : itemCount < 5 ? "товара" : "товаров"}
          </span>
        </div>
        <span className="font-semibold" data-testid="text-cart-total">
          {total.toFixed(2)}₽
        </span>
      </Button>
    </div>
  );
}
