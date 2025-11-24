import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState, useEffect } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { useLocation } from "wouter";
import type { Order } from "@shared/schema";
import { queryClient, apiRequest } from "@/lib/queryClient";
import { useToast } from "@/hooks/use-toast";
import { Package, ShoppingCart, LogOut, Loader2 } from "lucide-react";

export default function AdminPage() {
  const [, setLocation] = useLocation();
  const { toast } = useToast();
  const [offlineUserCode, setOfflineUserCode] = useState("");
  const [offlineCheburekCount, setOfflineCheburekCount] = useState("1");
  const [isCheckingAuth, setIsCheckingAuth] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await fetch("/api/admin/check-auth");
        const data = await response.json();
        if (!data.isAuthenticated) {
          setLocation("/admin/login");
        }
      } catch (error) {
        setLocation("/admin/login");
      } finally {
        setIsCheckingAuth(false);
      }
    };
    checkAuth();
  }, [setLocation]);

  const { data: allOrders = [] } = useQuery<Order[]>({
    queryKey: ["/api/admin/orders"],
    enabled: !isCheckingAuth,
  });

  const logoutMutation = useMutation({
    mutationFn: async () => {
      await apiRequest("POST", "/api/admin/logout");
    },
    onSuccess: () => {
      toast({
        title: "Выход выполнен",
        description: "До свидания!",
      });
      setLocation("/admin/login");
    },
  });

  const updateOrderStatusMutation = useMutation({
    mutationFn: async ({ orderId, status }: { orderId: string; status: string }) => {
      const response = await fetch(`/api/orders/${orderId}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status }),
      });

      if (!response.ok) throw new Error("Failed to update status");
      return response.json();
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["/api/admin/orders"] });
      queryClient.invalidateQueries({ queryKey: ["/api/loyalty"] });
      const isCompleted = variables.status === "completed";
      toast({
        title: "Статус обновлен",
        description: isCompleted 
          ? "Заказ завершен. Бонусы начислены!" 
          : "Статус заказа успешно изменен",
      });
    },
  });

  const offlinePurchaseMutation = useMutation({
    mutationFn: async () => {
      const count = parseInt(offlineCheburekCount);
      if (isNaN(count) || count <= 0) {
        throw new Error("Invalid cheburek count");
      }
      
      const response = await fetch("/api/admin/offline-purchase", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          userCode: offlineUserCode.toUpperCase(), 
          cheburekCount: count
        }),
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || "Failed to record purchase");
      }
      return response.json();
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["/api/loyalty"] });
      setOfflineUserCode("");
      setOfflineCheburekCount("1");
      toast({
        title: "Покупка зарегистрирована!",
        description: "Бонусы начислены в программу лояльности",
      });
    },
    onError: (error: any) => {
      toast({
        title: "Ошибка",
        description: error.message || "Не удалось зарегистрировать покупку",
        variant: "destructive",
      });
    },
  });

  const getStatusLabel = (status: string) => {
    const labels: Record<string, string> = {
      pending: "Ожидание",
      preparing: "Готовится",
      ready: "Готов",
      completed: "Завершен",
    };
    return labels[status] || status;
  };

  if (isCheckingAuth) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="min-h-screen pb-24">
      <header className="sticky top-0 z-30 bg-background/95 backdrop-blur-md border-b">
        <div className="max-w-6xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-2xl font-semibold">Панель администратора</h1>
              <p className="text-sm text-muted-foreground">
                Управление заказами и кодами лояльности
              </p>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => logoutMutation.mutate()}
              disabled={logoutMutation.isPending}
              data-testid="button-logout"
            >
              <LogOut className="w-4 h-4 mr-2" />
              Выход
            </Button>
          </div>
        </div>
      </header>

      <div className="max-w-6xl mx-auto px-4 py-6">
        <Tabs defaultValue="orders" className="w-full">
          <TabsList className="grid w-full max-w-2xl grid-cols-2">
            <TabsTrigger value="orders" className="flex items-center gap-2">
              <Package className="w-4 h-4" />
              Заказы
            </TabsTrigger>
            <TabsTrigger value="offline" className="flex items-center gap-2">
              <ShoppingCart className="w-4 h-4" />
              Офлайн
            </TabsTrigger>
          </TabsList>

          <TabsContent value="orders" className="space-y-4 mt-6">
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {allOrders.map((order) => (
                <Card key={order.id} className="p-4 space-y-3">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="font-mono text-xs font-semibold">
                        {order.id.slice(0, 8).toUpperCase()}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {new Date(order.createdAt).toLocaleString('ru-RU')}
                      </p>
                    </div>
                    <Badge variant={order.status === "completed" ? "secondary" : "default"}>
                      {getStatusLabel(order.status)}
                    </Badge>
                  </div>

                  <div className="pt-2 border-t">
                    <p className="text-lg font-semibold">{order.total}₽</p>
                    {order.notes && (
                      <p className="text-sm text-muted-foreground mt-1">
                        Примечание: {order.notes}
                      </p>
                    )}
                  </div>

                  {order.status !== "completed" && (
                    <div className="flex gap-2">
                      {order.status === "pending" && (
                        <Button
                          size="sm"
                          className="flex-1"
                          onClick={() =>
                            updateOrderStatusMutation.mutate({
                              orderId: order.id,
                              status: "preparing",
                            })
                          }
                        >
                          Начать готовить
                        </Button>
                      )}
                      {order.status === "preparing" && (
                        <Button
                          size="sm"
                          className="flex-1"
                          onClick={() =>
                            updateOrderStatusMutation.mutate({
                              orderId: order.id,
                              status: "ready",
                            })
                          }
                        >
                          Готов
                        </Button>
                      )}
                      {order.status === "ready" && (
                        <Button
                          size="sm"
                          variant="outline"
                          className="flex-1"
                          onClick={() =>
                            updateOrderStatusMutation.mutate({
                              orderId: order.id,
                              status: "completed",
                            })
                          }
                        >
                          Завершить
                        </Button>
                      )}
                    </div>
                  )}
                </Card>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="offline" className="space-y-4 mt-6">
            <Card className="p-6 space-y-4">
              <div>
                <h2 className="text-lg font-semibold mb-2">Регистрация офлайн-покупки</h2>
                <p className="text-sm text-muted-foreground">
                  Введите количество купленных чебуреков для начисления бонусов
                </p>
              </div>

              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="user-code">Код пользователя</Label>
                  <Input
                    id="user-code"
                    value={offlineUserCode}
                    onChange={(e) => setOfflineUserCode(e.target.value.toUpperCase())}
                    placeholder="Введите 4-значный код (например: A1B2)"
                    maxLength={4}
                    data-testid="input-offline-user-code"
                  />
                  <p className="text-xs text-muted-foreground">
                    4-значный код пользователя (буквы и цифры)
                  </p>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="cheburek-count">Количество чебуреков</Label>
                  <Input
                    id="cheburek-count"
                    type="number"
                    min="1"
                    value={offlineCheburekCount}
                    onChange={(e) => setOfflineCheburekCount(e.target.value)}
                    placeholder="1"
                    data-testid="input-offline-cheburek-count"
                  />
                </div>

                <Button
                  className="w-full"
                  onClick={() => offlinePurchaseMutation.mutate()}
                  disabled={offlinePurchaseMutation.isPending}
                  data-testid="button-submit-offline-purchase"
                >
                  {offlinePurchaseMutation.isPending ? "Обработка..." : "Зарегистрировать покупку"}
                </Button>
              </div>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}
