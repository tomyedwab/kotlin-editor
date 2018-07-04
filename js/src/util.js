export const capitalize = word => word.substr(0, 1).toUpperCase() + word.substr(1);

export function debounce(fn, timeout) {
    let timer = null;
    return function() {
        const args = arguments;
        const self = this;
        if (timer) {
            clearTimeout(timer);
        }
        timer = setTimeout(() => {
            fn.apply(self, args);
            timer = null;
        }, timeout);
    };
}

