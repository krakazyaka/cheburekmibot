import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Separator } from "@/components/ui/separator";
import { ArrowLeft, Minus, Plus } from "lucide-react";
import { useState, useEffect } from "react";
import { useLocation } from "wouter";
import { useCart } from "@/contexts/CartContext";
import { useTelegram } from "@/contexts/TelegramContext";
import { useQuery, useMutation } from "@tanstack/react-query";
import type { MenuItem, User } from "@shared/schema";
import { apiRequest, queryClient } from "@/lib/queryClient";
import { useToast } from "@/hooks/use-toast";

export default function CheckoutPage() {
  const [, setLocation] = useLocation();
  const { cart, addToCart, clearCart } = useCart();
  const [notes, setNotes] = useState("");
  const { toast } = useToast();
  const { user: telegramUser } = useTelegram();
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [isLoadingUser, setIsLoadingUser] = useState(false);
  const [userLoadError, setUserLoadError] = useState(false);

  const { data: menuItems = [] } = useQuery<MenuItem[]>({
    queryKey: ["/api/menu"],
  });

  useEffect(() => {
    if (telegramUser && !currentUser && !isLoadingUser && !userLoadError) {
      setIsLoadingUser(true);
      fetch("/api/users/find-or-create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          telegramId: telegramUser.id.toString(),
          firstName: telegramUser.first_name,
          lastName: telegramUser.last_name,
          username: telegramUser.username,
        }),
      })
        .then((res) => res.json())
        .then((user) => {
          setCurrentUser(user);
          setIsLoadingUser(false);
        })
        .catch((error) => {
          console.error("Failed to find/create user:", error);
          setIsLoadingUser(false);
          setUserLoadError(true);
          toast({
            title: "Ошибка",
            description: "Не удалось создать пользователя. Попробуйте перезагрузить страницу.",
            variant: "destructive",
          });
        });
    }
  }, [telegramUser, currentUser, isLoadingUser, userLoadError, toast]);

  const retryUserLoad = () => {
    setUserLoadError(false);
  };

  const placeOrderMutation = useMutation({
    mutationFn: async (orderData: any) => {
      const response = await fetch("/api/orders", {
        method: "POST",
        body: JSON.stringify(orderData),
        headers: { "Content-Type": "application/json" },
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to place order: ${response.status}`);
      }
      return response.json();
    },
    onSuccess: (data: any) => {
      clearCart();
      queryClient.invalidateQueries({ queryKey: ["/api/users"] });
      setLocation(`/confirmation?orderId=${data.id}`);
    },
    onError: (error: Error) => {
      toast({
        title: "Ошибка заказа",
        description: error.message || "Не удалось разместить заказ. Попробуйте снова.",
        variant: "destructive",
      });
    },
  });

  const cartItems = cart.map((cartItem) => {
    const isXL = cartItem.isXL;
    const itemId = cartItem.id.replace('-xl', '');
    const menuItem = menuItems.find((m) => m.id === itemId);
    if (!menuItem) return null;
    
    let price = parseFloat(menuItem.price);
    if (isXL && menuItem.hasXL) {
      price = price * 1.5;
    }
    
    return {
      ...cartItem,
      menuItem,
      price,
      displayName: menuItem.name + (isXL ? " (XL)" : ""),
    };
  }).filter(Boolean);

  const subtotal = cartItems.reduce((sum, item) => {
    if (!item) return sum;
    return sum + item.price * item.quantity;
  }, 0);
  const tax = subtotal * 0.08;
  const total = subtotal + tax;

  const updateQuantity = (itemId: string, delta: number) => {
    const item = cart.find((i) => i.id === itemId);
    if (!item) return;
    
    const newQuantity = item.quantity + delta;
    if (newQuantity <= 0) {
      return;
    }
    
    const menuItemId = itemId.replace('-xl', '');
    addToCart(menuItemId, delta, item.isXL);
  };

  const handleCheckout = () => {
    if (cart.length === 0 || !currentUser) return;
    
    const orderItems = cartItems.map((item) => {
      if (!item) return null;
      return {
        menuItemId: item.menuItem.id,
        name: item.displayName,
        price: item.price.toFixed(2),
        quantity: item.quantity,
        isXL: item.isXL || false,
      };
    }).filter(Boolean);

    const orderData = {
      userId: currentUser.id,
      status: "pending",
      subtotal: subtotal.toFixed(2),
      tax: tax.toFixed(2),
      total: total.toFixed(2),
      notes: notes || null,
      items: orderItems,
    };

    placeOrderMutation.mutate(orderData);
  };

  if (cart.length === 0) {
    return (
      <div className="min-h-screen pb-24 flex flex-col items-center justify-center px-4">
        <h2 className="text-xl font-semibold mb-2">Корзина пуста</h2>
        <p className="text-muted-foreground text-center mb-6">
          Добавьте блюда из меню, чтобы оформить заказ
        </p>
        <Button onClick={() => setLocation("/")}>
          Вернуться в меню
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen pb-24">
      <header className="sticky top-0 z-30 bg-background/95 backdrop-blur-md border-b">
        <div className="max-w-lg mx-auto px-4 py-4 flex items-center gap-3">
          <Button
            size="icon"
            variant="ghost"
            onClick={() => setLocation("/")}
            data-testid="button-back"
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div>
            <h1 className="text-2xl font-semibold" data-testid="text-page-title">
              Оформление заказа
            </h1>
            <p className="text-sm text-muted-foreground">
              Проверьте ваш заказ
            </p>
          </div>
        </div>
      </header>

      <div className="max-w-lg mx-auto px-4 py-6 space-y-6">
        <Card className="p-4 space-y-4">
          <h2 className="font-semibold">Ваш заказ</h2>
          {cartItems.map((item) => {
            if (!item) return null;
            return (
              <div key={item.id} className="flex gap-3" data-testid={`item-${item.id}`}>
                <img
                  src={item.menuItem.image}
                  alt={item.displayName}
                  className="w-16 h-16 rounded-lg object-cover"
                />
                <div className="flex-1 min-w-0">
                  <h3 className="font-medium line-clamp-1" data-testid="text-item-name">
                    {item.displayName}
                  </h3>
                  <p className="text-sm text-primary" data-testid="text-item-price">
                    {item.price.toFixed(2)}₽
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  <Button
                    size="icon"
                    variant="outline"
                    className="h-8 w-8"
                    onClick={() => updateQuantity(item.id, -1)}
                    data-testid={`button-decrease-${item.id}`}
                  >
                    <Minus className="w-3 h-3" />
                  </Button>
                  <span className="w-6 text-center" data-testid={`text-quantity-${item.id}`}>
                    {item.quantity}
                  </span>
                  <Button
                    size="icon"
                    variant="outline"
                    className="h-8 w-8"
                    onClick={() => updateQuantity(item.id, 1)}
                    data-testid={`button-increase-${item.id}`}
                  >
                    <Plus className="w-3 h-3" />
                  </Button>
                </div>
              </div>
            );
          })}
        </Card>

        <Card className="p-4 space-y-3">
          <h2 className="font-semibold">Комментарий к заказу</h2>
          <Textarea
            placeholder="Особые пожелания? (необязательно)"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            className="resize-none"
            rows={3}
            data-testid="input-order-notes"
          />
        </Card>

        <Card className="p-4 space-y-3">
          <h2 className="font-semibold">Итого</h2>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-muted-foreground">Сумма</span>
              <span data-testid="text-subtotal">{subtotal.toFixed(2)}₽</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Налог (8%)</span>
              <span data-testid="text-tax">{tax.toFixed(2)}₽</span>
            </div>
            <Separator />
            <div className="flex justify-between text-base font-semibold">
              <span>Всего</span>
              <span data-testid="text-total">{total.toFixed(2)}₽</span>
            </div>
          </div>
        </Card>

        {userLoadError && (
          <Card className="p-4 bg-destructive/10 border-destructive">
            <p className="text-sm text-destructive mb-2">
              Не удалось загрузить данные пользователя
            </p>
            <Button
              variant="outline"
              size="sm"
              onClick={retryUserLoad}
              data-testid="button-retry-user-load"
            >
              Повторить попытку
            </Button>
          </Card>
        )}

        <Button
          className="w-full"
          size="lg"
          onClick={handleCheckout}
          disabled={placeOrderMutation.isPending || isLoadingUser || !currentUser || userLoadError}
          data-testid="button-place-order"
        >
          {isLoadingUser ? "Загрузка..." : placeOrderMutation.isPending ? "Оформление..." : `Разместить заказ - ${total.toFixed(2)}₽`}
        </Button>
      </div>
    </div>
  );
}
