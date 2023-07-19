export enum AcoesEnum {
    ADICIONAR = 'ADICIONAR',
    EXIBIR = 'EXIBIR',
    EDITAR = 'EDITAR'
  }

export function obterValorEnum(valor: string): AcoesEnum | undefined {
  const valoresEnum = Object.values(AcoesEnum);
  const valorUppercase = valor.toUpperCase();
  const valorEncontrado = valoresEnum.find((enumValor) => enumValor === valorUppercase);

  if (valorEncontrado) {
    return valorEncontrado as AcoesEnum;
  }

  return undefined;
}
  