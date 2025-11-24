import { Card } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { User as UserIcon, Award, ShoppingBag, Copy } from "lucide-react";
import { useTelegram } from "@/contexts/TelegramContext";
import { useQuery } from "@tanstack/react-query";
import type { User, Order, LoyaltyCard } from "@shared/schema";
import { useToast } from "@/hooks/use-toast";
import { Loader2 } from "lucide-react";

export default function ProfilePage() {
  const { user: telegramUser, isReady } = useTelegram();
  const { toast } = useToast();

  const { data: user, isLoading: loadingUser } = useQuery<User>({
    queryKey: ["/api/users/find-or-create"],
    enabled: !!telegramUser,
    queryFn: async () => {
      const response = await fetch("/api/users/find-or-create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          telegramId: telegramUser!.id.toString(),
          firstName: telegramUser!.first_name,
          lastName: telegramUser!.last_name,
          username: telegramUser!.username,
        }),
      });
      return response.json();
    },
  });

  const { data: orders = [] } = useQuery<Order[]>({
    queryKey: ["/api/users", user?.id, "orders"],
    enabled: !!user,
  });

  const { data: loyaltyCard } = useQuery<LoyaltyCard>({
    queryKey: ["/api/loyalty", user?.id],
    enabled: !!user,
  });

  const copyUserCode = () => {
    if (user?.userCode) {
      navigator.clipboard.writeText(user.userCode);
      toast({
        title: "Скопировано!",
        description: "Код пользователя скопирован в буфер обмена",
      });
    }
  };

  const getInitials = () => {
    if (!user) return "??";
    return `${user.firstName[0]}${user.lastName?.[0] || ""}`.toUpperCase();
  };

  if (!isReady || loadingUser) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="min-h-screen pb-24">
      <header className="sticky top-0 z-30 bg-background/95 backdrop-blur-md border-b">
        <div className="max-w-lg mx-auto px-4 py-4">
          <h1 className="text-2xl font-semibold" data-testid="text-page-title">
            Профиль
          </h1>
          <p className="text-sm text-muted-foreground">
            Управление аккаунтом
          </p>
        </div>
      </header>

      <div className="max-w-lg mx-auto px-4 py-6 space-y-6">
        <Card className="p-6 space-y-4">
          <div className="flex items-center gap-4">
            <Avatar className="w-16 h-16">
              <AvatarFallback className="text-lg">{getInitials()}</AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <h2 className="text-xl font-semibold" data-testid="text-user-name">
                {user?.firstName} {user?.lastName || ""}
              </h2>
              {user?.username && (
                <p className="text-sm text-muted-foreground" data-testid="text-user-username">
                  @{user.username}
                </p>
              )}
            </div>
          </div>

          <div className="pt-4 border-t">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-muted-foreground mb-1">Ваш код для офлайн покупок</p>
                <div className="flex items-center gap-2">
                  <Badge variant="outline" className="text-2xl font-mono px-4 py-2 tracking-widest" data-testid="text-user-code">
                    {user?.userCode}
                  </Badge>
                  <button
                    onClick={copyUserCode}
                    className="p-2 hover-elevate active-elevate-2 rounded-md"
                    data-testid="button-copy-code"
                  >
                    <Copy className="w-4 h-4 text-muted-foreground" />
                  </button>
                </div>
              </div>
            </div>
            <p className="text-xs text-muted-foreground mt-2">
              Назовите этот код кассиру при офлайн покупке для начисления бонусов
            </p>
          </div>
        </Card>

        <div className="grid grid-cols-2 gap-3">
          <Card className="p-4 text-center">
            <ShoppingBag className="w-6 h-6 mx-auto mb-2 text-primary" />
            <p className="text-2xl font-semibold" data-testid="text-stat-orders">{orders.length}</p>
            <p className="text-xs text-muted-foreground">Заказов</p>
          </Card>
          <Card className="p-4 text-center">
            <Award className="w-6 h-6 mx-auto mb-2 text-primary" />
            <p className="text-2xl font-semibold" data-testid="text-stat-rewards">
              {loyaltyCard?.current || 0}/{loyaltyCard?.target || 10}
            </p>
            <p className="text-xs text-muted-foreground">Прогресс</p>
          </Card>
        </div>
      </div>
    </div>
  );
}
