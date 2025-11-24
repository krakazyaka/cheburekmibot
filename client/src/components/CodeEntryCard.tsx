import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { Check } from "lucide-react";

interface CodeEntryCardProps {
  onSubmit: (code: string) => void;
}

export default function CodeEntryCard({ onSubmit }: CodeEntryCardProps) {
  const [code, setCode] = useState("");
  const [isSuccess, setIsSuccess] = useState(false);

  const handleSubmit = () => {
    if (code.trim()) {
      onSubmit(code);
      setIsSuccess(true);
      setTimeout(() => {
        setIsSuccess(false);
        setCode("");
      }, 2000);
    }
  };

  return (
    <Card className="p-6">
      <h3 className="text-lg font-semibold mb-4" data-testid="text-code-entry-title">
        Enter Purchase Code
      </h3>
      <p className="text-sm text-muted-foreground mb-4">
        Enter the code from your receipt to track your cheburek purchase
      </p>

      <div className="space-y-3">
        <Input
          type="text"
          placeholder="Enter code"
          value={code}
          onChange={(e) => setCode(e.target.value.toUpperCase())}
          className="text-center text-lg font-mono"
          maxLength={8}
          data-testid="input-purchase-code"
        />
        <Button
          className="w-full"
          onClick={handleSubmit}
          disabled={!code.trim() || isSuccess}
          data-testid="button-submit-code"
        >
          {isSuccess ? (
            <>
              <Check className="w-4 h-4 mr-2" />
              Code Accepted!
            </>
          ) : (
            "Submit Code"
          )}
        </Button>
      </div>
    </Card>
  );
}
