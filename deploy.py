import os

script_dir = os.path.dirname(__file__)

base_dirs = [
             script_dir,
             os.path.join(script_dir, "product-api/"),
             os.path.join(script_dir, "shopping-api/"),
             os.path.join(script_dir, "user-api/")
             ]

for base_dir in base_dirs:
    compose_file = os.path.join(base_dir, "docker-compose.yml")

    if os.path.exists(compose_file):
        print(f"Deploy containers for {compose_file}")
        os.system(f"docker compose -f {compose_file} up -d")
    else:
        print(f"Docker-compose.yml File Not Found In {base_dir}")