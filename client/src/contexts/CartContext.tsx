import { createContext, useContext, useState } from "react";

interface CartItem {
  id: string;
  quantity: number;
  isXL?: boolean;
}

interface CartContextType {
  cart: CartItem[];
  addToCart: (menuItemId: string, quantity: number, isXL?: boolean) => void;
  clearCart: () => void;
}

const CartContext = createContext<CartContextType | null>(null);

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart must be used within CartProvider");
  }
  return context;
};

export const CartProvider = ({ children }: { children: React.ReactNode }) => {
  const [cart, setCart] = useState<CartItem[]>([]);

  const addToCart = (menuItemId: string, quantity: number, isXL?: boolean) => {
    const itemKey = isXL ? `${menuItemId}-xl` : menuItemId;
    setCart((prev) => {
      const existing = prev.find((item) => item.id === itemKey);
      if (existing) {
        const newQuantity = existing.quantity + quantity;
        if (newQuantity <= 0) {
          return prev.filter((item) => item.id !== itemKey);
        }
        return prev.map((item) =>
          item.id === itemKey
            ? { ...item, quantity: newQuantity }
            : item
        );
      }
      if (quantity > 0) {
        return [...prev, { id: itemKey, quantity, isXL }];
      }
      return prev;
    });
  };

  const clearCart = () => setCart([]);

  return (
    <CartContext.Provider value={{ cart, addToCart, clearCart }}>
      {children}
    </CartContext.Provider>
  );
};
