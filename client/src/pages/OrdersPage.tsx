import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Clock, CheckCircle2, Package } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import { useTelegram } from "@/contexts/TelegramContext";
import type { Order, OrderItem, User } from "@shared/schema";

interface OrderWithItems extends Order {
  items?: OrderItem[];
}

export default function OrdersPage() {
  const { user: telegramUser } = useTelegram();
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  useEffect(() => {
    if (telegramUser && !currentUser) {
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
        .then((user) => setCurrentUser(user))
        .catch((error) => console.error("Failed to find/create user:", error));
    }
  }, [telegramUser, currentUser]);

  const { data: orders = [], isLoading } = useQuery<Order[]>({
    queryKey: currentUser ? [`/api/users/${currentUser.id}/orders`] : [],
    enabled: !!currentUser,
  });

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "pending":
      case "preparing":
        return <Clock className="w-4 h-4" />;
      case "ready":
        return <Package className="w-4 h-4" />;
      case "completed":
        return <CheckCircle2 className="w-4 h-4" />;
      default:
        return null;
    }
  };

  const getStatusVariant = (status: string): "default" | "secondary" => {
    switch (status) {
      case "pending":
      case "preparing":
      case "ready":
        return "default";
      case "completed":
        return "secondary";
      default:
        return "secondary";
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case "pending":
        return "Ожидание";
      case "preparing":
        return "Готовится";
      case "ready":
        return "Готов";
      case "completed":
        return "Завершен";
      default:
        return status;
    }
  };

  const formatDate = (dateString: string | Date) => {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (days === 0) {
      return `Сегодня, ${date.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}`;
    } else if (days === 1) {
      return `Вчера, ${date.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}`;
    } else {
      return date.toLocaleDateString('ru-RU', { day: 'numeric', month: 'short', year: 'numeric' });
    }
  };

  if (isLoading) {
    return <div className="min-h-screen pb-24 flex items-center justify-center">Загрузка...</div>;
  }

  if (orders.length === 0) {
    return (
      <div className="min-h-screen pb-24 flex flex-col items-center justify-center px-4">
        <div className="w-48 h-48 mb-6 flex items-center justify-center text-8xl opacity-60">
          📋
        </div>
        <h2 className="text-xl font-semibold mb-2">Пока нет заказов</h2>
        <p className="text-muted-foreground text-center">
          Начните просматривать наше вкусное меню, чтобы сделать первый заказ
        </p>
      </div>
    );
  }

  return (
    <div className="min-h-screen pb-24">
      <header className="sticky top-0 z-30 bg-background/95 backdrop-blur-md border-b">
        <div className="max-w-lg mx-auto px-4 py-4">
          <h1 className="text-2xl font-semibold" data-testid="text-page-title">
            Мои заказы
          </h1>
          <p className="text-sm text-muted-foreground">
            Отслеживайте историю заказов
          </p>
        </div>
      </header>

      <div className="max-w-lg mx-auto px-4 py-6 space-y-4">
        {orders.map((order) => (
          <Card
            key={order.id}
            className="p-4 hover-elevate"
            data-testid={`card-order-${order.id}`}
          >
            <div className="flex items-start justify-between mb-3">
              <div>
                <h3 className="font-semibold text-xs font-mono" data-testid="text-order-id">
                  {order.id.slice(0, 8).toUpperCase()}
                </h3>
                <p className="text-sm text-muted-foreground" data-testid="text-order-date">
                  {formatDate(order.createdAt)}
                </p>
              </div>
              <Badge
                variant={getStatusVariant(order.status)}
                className="flex items-center gap-1"
                data-testid={`badge-order-status-${order.status}`}
              >
                {getStatusIcon(order.status)}
                {getStatusLabel(order.status)}
              </Badge>
            </div>

            <div className="flex items-center justify-between pt-3 border-t">
              <span className="font-semibold" data-testid="text-order-total">
                {order.total}₽
              </span>
              {(order.status === "pending" || order.status === "preparing") && (
                <span className="text-sm text-muted-foreground" data-testid="text-order-eta">
                  Готов через ~15-20 минут
                </span>
              )}
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
