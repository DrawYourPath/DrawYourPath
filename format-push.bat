@echo off
java -jar ./programs/ktlint.jar --disabled_rules no-wildcard-imports -F ./app/**.kt
git diff-index --quiet HEAD --
if %errorlevel% equ 0 (
    echo Code matches format.
) else (
	git add .
	git commit -m "format: auto format"
	git push
	echo Code formatted and pushed.
)
@echo on