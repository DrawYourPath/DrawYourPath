java -jar ./programs/ktlint.jar --disabled_rules no-wildcard-imports -F ./app/**.kt
if git diff-index --quiet HEAD --; then
    echo "Code formatting matched"
else
    git config --global user.email "bot@drawyourpath.ch"
    git config --global user.name "DrawYourPathBot"
    git add .
    git commit -m "action: auto-formatting"
    git push
    fi