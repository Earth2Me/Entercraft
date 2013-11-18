#!/bin/bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ARGS=(
	--exclude-dir=target,jar,lib
	--3
)

function has
{
	which "$@" > /dev/null 2>&1
	return $?
}

function fatal
{
	echo $'\e[31m'"$*"$'\e[m' >&2
	exit 1
}

if ! has cloc; then
	if has apt-get; then
		sudo apt-get install cloc -y || fatal 'cloc is required, but cannot be installed via apt-get.  Install it manually.'
	elif has yum; then
		sudo apt-get install cloc -y || fatal 'cloc is required, but cannot be installed via yum.  Install it manually.'
	else
		fatal 'cloc is required, but no compatible package manager was found that could be used to install it.  You will need to install cloc manually.'
	fi

	has cloc || fatal 'cloc is required, but automatic installation failed.  Install it manually.'
fi

if [ $# -gt 0 ]; then
	paths=( "$@" )
else
	paths=( "$DIR" )
fi

cloc "${ARGS[@]}" "${paths[@]}"
