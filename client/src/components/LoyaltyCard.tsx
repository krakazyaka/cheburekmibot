import { Card } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";

interface LoyaltyCardProps {
  current: number;
  target: number;
  freeAvailable?: number;
}

export default function LoyaltyCard({ current, target, freeAvailable = 0 }: LoyaltyCardProps) {
  const progress = (current / target) * 100;
  const remaining = target - current;

  return (
    <div className="space-y-4">
      {freeAvailable > 0 && (
        <Card className="p-4 bg-primary/10 border-primary">
          <div className="flex items-center gap-3">
            <span className="text-3xl">🎉</span>
            <div className="flex-1">
              <p className="font-semibold text-primary" data-testid="text-free-available">
                У вас {freeAvailable} бесплатных чебурека!
              </p>
              <p className="text-sm text-muted-foreground">
                Покажите этот экран при заказе
              </p>
            </div>
          </div>
        </Card>
      )}
      
      <Card className="p-6 bg-gradient-to-br from-primary/10 to-primary/5">
        <div className="flex items-start gap-4">
          <div className="w-16 h-16 flex items-center justify-center text-4xl" data-testid="img-cheburek-icon">
            🥟
          </div>
          <div className="flex-1">
            <h3 className="text-lg font-semibold mb-1" data-testid="text-loyalty-title">
              Бонусная программа
            </h3>
            <p className="text-sm text-muted-foreground" data-testid="text-loyalty-status">
              {remaining > 0
                ? `Еще ${remaining} чебурека до следующего бесплатного!`
                : "Вы заработали бесплатный чебурек! 🎉"}
            </p>
          </div>
        </div>

        <div className="mt-6 space-y-2">
          <div className="flex justify-between text-sm">
            <span className="text-muted-foreground">Прогресс</span>
            <span className="font-medium" data-testid="text-loyalty-progress">
              {current} / {target}
            </span>
          </div>
          <Progress value={progress} className="h-2" data-testid="progress-loyalty" />
        </div>

        <div className="mt-4 flex gap-2 flex-wrap">
          {Array.from({ length: target }).map((_, i) => (
            <div
              key={i}
              className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium ${
                i < current
                  ? "bg-primary text-primary-foreground"
                  : "bg-muted text-muted-foreground"
              }`}
              data-testid={`badge-stamp-${i}`}
            >
              {i + 1}
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
