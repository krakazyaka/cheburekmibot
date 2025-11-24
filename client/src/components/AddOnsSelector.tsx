import { Card } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import type { MenuItem } from "@shared/schema";

interface AddOnsSelectorProps {
  addOns: MenuItem[];
  selectedAddOns: Set<string>;
  onToggleAddOn: (addOnId: string) => void;
}

export default function AddOnsSelector({
  addOns,
  selectedAddOns,
  onToggleAddOn,
}: AddOnsSelectorProps) {
  if (addOns.length === 0) return null;

  return (
    <div className="space-y-3">
      <h3 className="font-semibold text-sm text-muted-foreground">Добавить к чебуреку:</h3>
      <div className="space-y-2">
        {addOns.map((addOn) => (
          <Card
            key={addOn.id}
            className="p-3 cursor-pointer hover-elevate active-elevate-2"
            onClick={() => onToggleAddOn(addOn.id)}
            data-testid={`card-addon-${addOn.id}`}
          >
            <div className="flex items-start gap-3">
              <Checkbox
                checked={selectedAddOns.has(addOn.id)}
                onCheckedChange={() => onToggleAddOn(addOn.id)}
                data-testid={`checkbox-addon-${addOn.id}`}
              />
              <div className="flex-1 min-w-0">
                <Label className="font-medium cursor-pointer">{addOn.name}</Label>
                <p className="text-sm text-muted-foreground mt-0.5">
                  {addOn.description}
                </p>
              </div>
              <span className="text-sm font-semibold whitespace-nowrap">
                +{parseFloat(addOn.price).toFixed(2)}₽
              </span>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
}
