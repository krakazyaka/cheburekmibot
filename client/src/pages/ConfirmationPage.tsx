import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CheckCircle2 } from "lucide-react";
import { useLocation } from "wouter";
import { useQuery } from "@tanstack/react-query";
import type { Order, OrderItem } from "@shared/schema";

export default function ConfirmationPage() {
  const [, setLocation] = useLocation();
  const orderId = new URLSearchParams(window.location.search).get('orderId');

  const { data: orderData } = useQuery<Order & { items: OrderItem[] }>({
    queryKey: ['/api/orders', orderId],
    enabled: !!orderId,
  });

  return (
    <div className="min-h-screen flex flex-col items-center justify-center px-4 pb-24">
      <div className="max-w-md w-full space-y-6 text-center">
        <div className="w-24 h-24 bg-primary/10 rounded-full flex items-center justify-center mx-auto">
          <CheckCircle2 className="w-12 h-12 text-primary" />
        </div>

        <div>
          <h1 className="text-3xl font-semibold mb-2" data-testid="text-confirmation-title">
            Заказ принят!
          </h1>
          <p className="text-muted-foreground">
            Ваш заказ получен и готовится
          </p>
        </div>

        <Card className="p-6 text-left space-y-4">
          <div className="flex justify-between">
            <span className="text-muted-foreground">Номер заказа</span>
            <span className="font-mono font-semibold text-xs" data-testid="text-order-number">
              {orderId?.slice(0, 8).toUpperCase() || "---"}
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-muted-foreground">Время приготовления</span>
            <span className="font-semibold" data-testid="text-estimated-time">
              15-20 минут
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-muted-foreground">Итого</span>
            <span className="font-semibold text-lg" data-testid="text-order-total">
              {orderData?.total || "0.00"}₽
            </span>
          </div>
        </Card>

        <div className="space-y-3">
          <Button
            className="w-full"
            onClick={() => setLocation("/orders")}
            data-testid="button-track-order"
          >
            Отследить заказ
          </Button>
          <Button
            variant="outline"
            className="w-full"
            onClick={() => setLocation("/")}
            data-testid="button-back-to-menu"
          >
            Вернуться в меню
          </Button>
        </div>
      </div>
    </div>
  );
}
