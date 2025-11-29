import { useState } from "react";
import CategoryChips from "@/components/CategoryChips";
import MenuItemCard from "@/components/MenuItemCard";
import ItemDetailDialog from "@/components/ItemDetailDialog";
import CartSummaryBar from "@/components/CartSummaryBar";
import { useLocation } from "wouter";
import { useQuery } from "@tanstack/react-query";
import type { MenuItem } from "@shared/schema";
import { useCart } from "@/contexts/CartContext";

export default function MenuPage() {
  const [, setLocation] = useLocation();
  const [selectedCategory, setSelectedCategory] = useState("CHEBUR");
  const [selectedItem, setSelectedItem] = useState<MenuItem | null>(null);
  const { cart, addToCart } = useCart();

  const { data: menuItems = [], isLoading } = useQuery<MenuItem[]>({
    queryKey: ["/api/menu"],
  });

  const filteredItems = menuItems.filter((item) => item.category === selectedCategory);
  const extraItems = menuItems.filter((item) => item.category === "extras");

  const handleAddToCart = (quantity: number, isXL?: boolean, selectedAddOns?: string[]) => {
    if (!selectedItem) return;
    addToCart(selectedItem.id, quantity, isXL);
    
    if (selectedAddOns && selectedAddOns.length > 0) {
      selectedAddOns.forEach((addOnId) => {
        addToCart(addOnId, quantity, false);
      });
    }
  };

  const cartTotal = cart.reduce((total, item) => {
    const isXL = item.isXL;
    const itemId = item.id.replace('-xl', '');
    const menuItem = menuItems.find((m) => m.id === itemId);
    if (!menuItem) return total;
    
    let price = parseFloat(menuItem.price);
    if (isXL && menuItem.hasXL) {
      price = price * 1.5;
    }
    
    return total + price * item.quantity;
  }, 0);

  const cartItemCount = cart.reduce((count, item) => count + item.quantity, 0);

  if (isLoading) {
    return <div className="min-h-screen flex items-center justify-center">Загрузка...</div>;
  }

  return (
    <div className="min-h-screen pb-32">
      <header className="sticky top-0 z-30 bg-background/95 backdrop-blur-md border-b">
        <div className="max-w-lg mx-auto px-4 py-4">
          <h1 className="text-2xl font-semibold" data-testid="text-page-title">
            Меню
          </h1>
          <p className="text-sm text-muted-foreground">
            Вкусная традиционная кухня
          </p>
        </div>
      </header>

      <div className="max-w-lg mx-auto">
        <div className="sticky top-[73px] z-20 bg-background py-4">
          <CategoryChips
            selectedCategory={selectedCategory}
            onSelectCategory={setSelectedCategory}
          />
        </div>

        <div className="px-4 space-y-3 mt-4">
          {filteredItems.map((item) => (
            <MenuItemCard
              key={item.id}
              id={item.id}
              name={item.name}
              description={item.description}
              price={parseFloat(item.price)}
              image={item.image}
              onClick={() => setSelectedItem(item)}
            />
          ))}
        </div>
      </div>

      {selectedItem && (
        <ItemDetailDialog
          open={!!selectedItem}
          onClose={() => setSelectedItem(null)}
          item={{
            id: selectedItem.id,
            name: selectedItem.name,
            description: selectedItem.description,
            price: parseFloat(selectedItem.price),
            image: selectedItem.image,
            hasXL: selectedItem.hasXL,
            category: selectedItem.category,
          }}
          addOns={selectedItem.category === "CHEBUR" ? extraItems : []}
          onAddToCart={handleAddToCart}
        />
      )}

      <CartSummaryBar
        itemCount={cartItemCount}
        total={cartTotal}
        onClick={() => setLocation("/checkout")}
      />
    </div>
  );
}
