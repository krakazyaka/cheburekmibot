import { Dialog, DialogContent } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Minus, Plus, X } from "lucide-react";
import { useState } from "react";
import AddOnsSelector from "./AddOnsSelector";
import type { MenuItem } from "@shared/schema";

interface ItemDetailDialogProps {
  open: boolean;
  onClose: () => void;
  item: {
    id: string;
    name: string;
    description: string;
    price: number;
    image: string;
    hasXL?: boolean;
    category?: string;
  };
  addOns?: MenuItem[];
  onAddToCart: (quantity: number, isXL?: boolean, selectedAddOns?: string[]) => void;
}

export default function ItemDetailDialog({
  open,
  onClose,
  item,
  addOns = [],
  onAddToCart,
}: ItemDetailDialogProps) {
  const [quantity, setQuantity] = useState(1);
  const [isXL, setIsXL] = useState(false);
  const [selectedAddOns, setSelectedAddOns] = useState<Set<string>>(new Set());

  const isCheburek = item.category === "chebureks";
  
  const toggleAddOn = (addOnId: string) => {
    setSelectedAddOns((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(addOnId)) {
        newSet.delete(addOnId);
      } else {
        newSet.add(addOnId);
      }
      return newSet;
    });
  };

  const addOnsTotal = Array.from(selectedAddOns).reduce((total, addOnId) => {
    const addOn = addOns.find((a) => a.id === addOnId);
    return total + (addOn ? parseFloat(addOn.price) : 0);
  }, 0);

  const basePrice = isXL ? item.price * 1.5 : item.price;
  const currentPrice = basePrice + addOnsTotal;

  const handleAddToCart = () => {
    onAddToCart(quantity, isXL, Array.from(selectedAddOns));
    setQuantity(1);
    setIsXL(false);
    setSelectedAddOns(new Set());
    onClose();
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md p-0 gap-0 overflow-y-auto max-h-[90vh]">
        <div className="relative">
          <img
            src={item.image}
            alt={item.name}
            className="w-full h-[280px] object-cover"
            data-testid="img-item-detail"
          />
          <Button
            size="icon"
            variant="secondary"
            className="absolute top-3 right-3 rounded-full shadow-lg z-10"
            onClick={onClose}
            data-testid="button-close-dialog"
          >
            <X className="w-4 h-4" />
          </Button>
        </div>

        <div className="p-6 space-y-4">
          <div>
            <h2 className="text-2xl font-semibold" data-testid="text-item-name">
              {item.name}
            </h2>
            <p className="text-muted-foreground mt-2" data-testid="text-item-description">
              {item.description}
            </p>
          </div>

          {item.hasXL && (
            <div className="flex gap-2">
              <Button
                variant={!isXL ? "default" : "outline"}
                className="flex-1"
                onClick={() => setIsXL(false)}
                data-testid="button-size-regular"
              >
                Обычный
              </Button>
              <Button
                variant={isXL ? "default" : "outline"}
                className="flex-1"
                onClick={() => setIsXL(true)}
                data-testid="button-size-xl"
              >
                XL (+50%)
              </Button>
            </div>
          )}

          {isCheburek && addOns.length > 0 && (
            <AddOnsSelector
              addOns={addOns}
              selectedAddOns={selectedAddOns}
              onToggleAddOn={toggleAddOn}
            />
          )}

          <div className="flex items-center justify-between pt-4">
            <span className="text-2xl font-semibold text-primary" data-testid="text-item-price">
              {currentPrice.toFixed(2)}₽
            </span>

            <div className="flex items-center gap-3">
              <Button
                size="icon"
                variant="outline"
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
                disabled={quantity <= 1}
                data-testid="button-decrease-quantity"
              >
                <Minus className="w-4 h-4" />
              </Button>
              <span className="text-lg font-medium w-8 text-center" data-testid="text-quantity">
                {quantity}
              </span>
              <Button
                size="icon"
                variant="outline"
                onClick={() => setQuantity(quantity + 1)}
                data-testid="button-increase-quantity"
              >
                <Plus className="w-4 h-4" />
              </Button>
            </div>
          </div>

          <Button
            className="w-full"
            size="lg"
            onClick={handleAddToCart}
            data-testid="button-add-to-cart"
          >
            Добавить в корзину - {(currentPrice * quantity).toFixed(2)}₽
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
