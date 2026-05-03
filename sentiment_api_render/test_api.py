"""Script de prueba para la API de sentimiento"""
import requests
import json

BASE_URL = "http://localhost:8000"

def test_health():
    print("=" * 50)
    print("TEST: /health")
    r = requests.get(f"{BASE_URL}/health")
    print(f"Status: {r.status_code}")
    print(f"Response: {json.dumps(r.json(), indent=2)}")

def test_predict_single():
    print("\n" + "=" * 50)
    print("TEST: /predict (individual)")
    
    samples = [
        ("El producto es excelente y llegó rápido, muy recomendado.", "ES Positivo"),
        ("Está bien, cumple, nada especial.", "ES Neutro"),
        ("No funciona, llegó roto y el soporte no responde.", "ES Negativo"),
        ("Produto excelente, entrega rápida e recomendo.", "PT Positivo"),
        ("Não funciona, veio quebrado e o suporte não responde.", "PT Negativo"),
    ]
    
    for text, expected in samples:
        r = requests.post(f"{BASE_URL}/predict", json={"text": text})
        result = r.json()
        print(f"\n[{expected}] -> {result['prevision']} ({result['probabilidad']:.2%})")
        print(f"   review_required: {result['review_required']}")

def test_predict_batch():
    print("\n" + "=" * 50)
    print("TEST: /predict/batch")
    
    texts = [
        "Excelente servicio, muy satisfecho",
        "Normal, nada del otro mundo",
        "Pésimo, no lo recomiendo"
    ]
    
    r = requests.post(f"{BASE_URL}/predict/batch", json={"texts": texts})
    results = r.json()["results"]
    
    for text, res in zip(texts, results):
        print(f"\n'{text[:40]}...'")
        print(f"   -> {res['prevision']} ({res['probabilidad']:.2%})")

if __name__ == "__main__":
    print("🧪 Probando API de Sentimiento\n")
    test_health()
    test_predict_single()
    test_predict_batch()
    print("\n" + "=" * 50)
    print("✅ Tests completados")
